package com.makibeans.service;

import com.makibeans.dto.CategoryRequestDTO;
import com.makibeans.dto.CategoryResponseDTO;
import com.makibeans.dto.CategoryUpdateDTO;
import com.makibeans.exceptions.CategoryInUseException;
import com.makibeans.exceptions.CircularReferenceException;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.filter.SearchFilter;
import com.makibeans.mapper.CategoryMapper;
import com.makibeans.model.Category;
import com.makibeans.model.Product;
import com.makibeans.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;

import static com.makibeans.util.UpdateUtils.*;

@Service
public class CategoryService extends AbstractCrudService<Category, Long> {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductService productService;
    private final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    public CategoryService(JpaRepository<Category, Long> repository, CategoryRepository categoryRepository, CategoryMapper categoryMapper, @Lazy ProductService productService) {
        super(repository);
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.productService = productService;
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id the ID of the category to retrieve.
     * @return the CategoryResponseDTO representing the category.
     * @throws IllegalArgumentException  if the id is null.
     * @throws ResourceNotFoundException if the category does not exist.
     */

    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = findById(id);
        return categoryMapper.toResponseDTO(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> findBySearchQuery(Map<String, String> searchParams) {

        Map<String, Function<Category, String>> searchableFields = Map.of(
                "name", Category::getName,
                "description", Category::getDescription);

        Map<String, Comparator<Category>> sortFields = Map.of(
                "id", Comparator.comparing(Category::getId, Comparator.nullsLast(Comparator.naturalOrder())),
                "name", Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER),
                "description", Comparator.comparing(Category::getDescription, String.CASE_INSENSITIVE_ORDER));

        List<Category> matchedCategories = SearchFilter.apply(findAll(),
                searchParams,
                searchableFields,
                sortFields);

        return matchedCategories.stream()
                .map(categoryMapper::toResponseDTO)
                .toList();
    }

    /**
     * Retrieves a list of categories by the given parent category ID.
     *
     * @param categoryId the ID of the parent category.
     * @return a list of categories that have the specified parent category.
     */

    private List<Category> getByParentCategoryId(Long categoryId) {
        return categoryRepository.findByParentCategoryId(categoryId);
    }


    /**
     * Creates a new root category.
     *
     * @param updateDTO the DTO containing the category details.
     * @return the newly created root category as categoryResponseDTO.
     * @throws IllegalArgumentException   if the name is null or empty.
     * @throws DuplicateResourceException if a root category with the given name already exists.
     */

    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO updateDTO) {

        Category category = categoryMapper.toEntity(updateDTO);
        Long parentCategoryId = updateDTO.getParentCategoryId();

        // If creating root category check uniqueness
        if (parentCategoryId == null) {
            if (categoryRepository.existsByNameAndParentCategory(category.getName(), null)) {
                throw new DuplicateResourceException("Root category with name " + category.getName() + " already exists.");
            }
        } else {
            Category parentCategory = categoryRepository.findById(parentCategoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category with id " + parentCategoryId + " not found."));

            validateUniqueCategoryNameWithinHierarchy(parentCategory, category.getName(), null);
            category.setParentCategory(parentCategory);
            parentCategory.getSubCategories().add(category);
        }

        Category createdCategory = categoryRepository.save(category);

        return categoryMapper.toResponseDTO(createdCategory);
    }

    /**
     * Deletes a category by its ID.
     *
     * @param categoryId the ID of the category to delete; must not be null.
     * @throws IllegalArgumentException  if the categoryId is null.
     * @throws ResourceNotFoundException if the category does not exist.
     * @throws CategoryInUseException    if category is in use by products.
     */

    @Transactional
    public void deleteCategory(Long categoryId) {

        List<Product> products = getAllRelatedProductsByCategoryId(categoryId);

        if (!products.isEmpty()) {
            String productsString = products
                    .stream()
                    .map(Product::getProductName )
                    .toList()
                    .toString();

            throw new CategoryInUseException("Category cannot be deleted because it is in use by the following products:  " + productsString + ". Please re-assign products before deleting the category.");
        }
    }

    /**
     * Updates an existing category.
     *
     * @param id         the ID of the category to update.
     * @param updateDTO the DTO containing the updated category details.
     * @return the updated category as a CategoryResponseDTO.
     * @throws ResourceNotFoundException  if the category with the given ID does not exist.
     * @throws DuplicateResourceException if the updated category name already exists within the hierarchy.
     * @throws CircularReferenceException if updating the parent category creates a circular reference.
     */

    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryUpdateDTO updateDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID " + id + " not found."));

        updateCategoryName(category, updateDTO.getName());
        updateCategoryDescription(category, updateDTO.getDescription());
        updateCategoryImageUrl(category, updateDTO.getImageUrl());
        updateCategoryParent(category, updateDTO.getParentCategoryId());

        return categoryMapper.toResponseDTO(update(id, category));
    }

    /**
     * Validates if setting a parent category would create a circular reference.
     *
     * @param parentCategory the new parent category.
     * @param subCategory    the subcategory to validate.
     * @throws CircularReferenceException if a circular reference is detected.
     */

    void validateCircularReference(Category parentCategory, Category subCategory) {
        Category current = parentCategory;

        while (current != null) {
            if (current.equals(subCategory)) {
                throw new CircularReferenceException(String.format(
                        "Category '%s' cannot be assigned as a subcategory of category '%s' because it would create a circular reference.",
                        subCategory.getName(), parentCategory.getName()));
            }
            current = current.getParentCategory(); // Traverse up the hierarchy
        }
    }

    /**
     * Validates that a category name is unique within its hierarchy.
     *
     * @param parentCategory  the parent category to check.
     * @param categoryName    the name to validate.
     * @param currentCategory the current category being validated.
     * @throws DuplicateResourceException if a duplicate name is found.
     */

    void validateUniqueCategoryNameWithinHierarchy(Category parentCategory, String categoryName, Category currentCategory) {

        Category current = parentCategory;

        //check if category already exists under same parent category
        if (parentCategory != null) {
            for (Category subCategory : parentCategory.getSubCategories()) {

                if (!subCategory.equals(currentCategory) && categoryName.equalsIgnoreCase(subCategory.getName())) {
                    throw new DuplicateResourceException("Category name " + categoryName + " already exists under parent category " + parentCategory.getName() + ".");
                }
            }
        }

        // check if category name exist in hierarchy of parent categories
        while (current != null) {
            if (current.getName().equalsIgnoreCase(categoryName) && !current.equals(currentCategory)) {
                throw new DuplicateResourceException(
                        String.format("Category '%s' already exists in the hierarchy of parent categories.", current.getName())
                );
            }
            current = current.getParentCategory(); // Move to the next parent
        }
    }

    /**
     * Retrieves all products related to a given category, including products in its subcategories.
     *
     * @param categoryId the ID of the category to retrieve products for.
     * @return a list of products related to the specified category.
     */

    private List<Product> getAllRelatedProductsByCategoryId(Long categoryId) {
        // Find products for the given category
        List<Product> currentProducts= productService.getProductsByCategoryId(categoryId);
        List<Product> products = new ArrayList<>(currentProducts);

        // Find subcategories and their products
        List<Category> subCategories = getByParentCategoryId(categoryId);
        for (Category subCategory : subCategories) {
            List<Product> subCategoryProducts = productService.getProductsByCategoryId(subCategory.getId());
            products.addAll(subCategoryProducts);
        }

        logger.info("Found {} products{}", products.size(), products.stream().map(Product::getProductName).toList());

        return products;
    }

    /**
     * Updates the category name if needed.
     *
     * @param category the category to update.
     * @param newName  the new name to set.
     */
    private void updateCategoryName(Category category, String newName) {
        if (shouldUpdate(newName, category.getName())) {
            validateUniqueCategoryNameWithinHierarchy(category.getParentCategory(), newName, category);
            category.setName(normalize(newName));
        }
    }

    /**
     * Updates the category description if needed.
     *
     * @param category      the category to update.
     * @param newDescription the new description to set.
     */
    private void updateCategoryDescription(Category category, String newDescription) {
        if (shouldUpdate(newDescription, category.getDescription())) {
            category.setDescription(normalize(newDescription));
        }
    }

    /**
     * Updates the category image URL if needed.
     *
     * @param category  the category to update.
     * @param newImageUrl the new image URL to set.
     */
    private void updateCategoryImageUrl(Category category, String newImageUrl) {
        if (shouldUpdate(newImageUrl, category.getImageUrl())) {
            category.setImageUrl(normalize(newImageUrl));
        }
    }

    /**
     * Updates the parent category if needed.
     *
     * @param category         the category to update.
     * @param newParentCategoryId the new parent category ID to set.
     */
    private void updateCategoryParent(Category category, Long newParentCategoryId) {
        if (newParentCategoryId != null &&
                (category.getParentCategory() == null ||
                        !newParentCategoryId.equals(category.getParentCategory().getId()))) {

            Category newParent = categoryRepository.findById(newParentCategoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found."));
            validateCircularReference(newParent, category);
            category.setParentCategory(newParent);
        }
    }
}
