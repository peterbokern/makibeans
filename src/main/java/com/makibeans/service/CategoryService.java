package com.makibeans.service;
import com.makibeans.dto.CategoryRequestDTO;
import com.makibeans.dto.CategoryResponseDTO;
import com.makibeans.exeptions.CircularReferenceException;
import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.mapper.CategoryMapper;
import com.makibeans.model.Category;
import com.makibeans.model.Product;
import com.makibeans.repository.AttributeValueRepository;
import com.makibeans.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class CategoryService extends AbstractCrudService<Category, Long> {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryService(JpaRepository<Category, Long> repository, CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        super(repository);
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id the ID of the category to retrieve.
     * @return the CategoryResponseDTO representing the category.
     * @throws IllegalArgumentException if the id is null.
     * @throws ResourceNotFoundException if the category does not exist.
     */

    @Transactional
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = findById(id);
        return categoryMapper.toResponseDTO(category);
    }

    /**
     * Retrieves all categories.
     *
     * @return a list of CategoryResponseDTO representing all categories.
     */

    @Transactional
    public List<CategoryResponseDTO> getAllCategories(){
        return categoryRepository.findAll().stream().map(categoryMapper:: toResponseDTO).toList();
    }

    /**
     * Creates a new root category.
     *
     * @param requestDTO the DTO containing the category details.
     * @return the newly created root category as categoryResponseDTO.
     * @throws IllegalArgumentException      if the name is null or empty.
     * @throws DuplicateResourceException   if a root category with the given name already exists.
     */

    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO) {

        Category category = categoryMapper.toEntity(requestDTO);
        Long parentCategoryId = requestDTO.getParentCategoryId();

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
     * @throws IllegalArgumentException if the categoryId is null.
     * @throws ResourceNotFoundException if the category does not exist.
     */

    @Transactional
    public void deleteCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category id " + categoryId + " not found."));

        //Remove reference to deleted category
        for (Product product : category.getProducts()) {
            product.setCategory(null);
        }

        delete(categoryId);
    }

    /**
     * Updates an existing category.
     *
     * @param id the ID of the category to update.
     * @param requestDTO the DTO containing the updated category details.
     * @return the updated category as a CategoryResponseDTO.
     * @throws ResourceNotFoundException if the category with the given ID does not exist.
     * @throws DuplicateResourceException if the updated category name already exists within the hierarchy.
     * @throws CircularReferenceException if updating the parent category creates a circular reference.
     */

    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID " + id + " not found."));

        validateUniqueCategoryNameWithinHierarchy(category.getParentCategory(), requestDTO.getName(), category);

        // update category
        category.setName(requestDTO.getName());
        category.setDescription(requestDTO.getDescription());
        category.setImageUrl(requestDTO.getImageUrl());

        // Set new parent category
        Long newParentId = requestDTO.getParentCategoryId();
        if (newParentId != null) {
            Category newParent = categoryRepository.findById(newParentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category with ID " + newParentId + " not found."));
            validateCircularReference(newParent, category);
            category.setParentCategory(newParent);
        } else {
            category.setParentCategory(null); // Set as root category if null
        }

        Category updatedCategory = update(id, category);

        return categoryMapper.toResponseDTO(updatedCategory);
    }

    /**
     * Validates if setting a parent category would create a circular reference.
     *
     * @param parentCategory the new parent category.
     * @param subCategory  the subcategory to validate.
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
     * @param parentCategory the parent category to check.
     * @param categoryName   the name to validate.
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

}
