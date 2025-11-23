package com.makibeans.service.impl;

import com.makibeans.dto.category.CategoryRequestDTO;
import com.makibeans.dto.category.CategoryUpdateDTO;
import com.makibeans.exceptions.*;
import com.makibeans.mapper.CategoryMapper;
import com.makibeans.model.Category;
import com.makibeans.repository.CategoryRepository;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import com.makibeans.search.filters.CategoryFilter;
import com.makibeans.service.service.CategoryAttributeService;
import com.makibeans.service.service.CategoryService;
import com.makibeans.service.service.CrudService;
import com.makibeans.product.service.ProductService;
import com.makibeans.util.ImageUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.makibeans.util.UpdateUtils.*;

/**
 * Service class for managing Category entities.
 * Provides methods to perform CRUD operations and search for Categories.
 */

@Service
public class CategoryServiceImpl implements CategoryService, CrudService<Category, Long> {

    private final CategoryRepository repo;
    private final CategoryMapper mapper;
    private final ProductService productService;
    private final CategoryAttributeService categoryAttributeService;
    private final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    private final ImageUtils imageUtils;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper, @Lazy ProductService productService, @Lazy CategoryAttributeService categoryAttributeService, ImageUtils imageUtils) {
        this.repo = categoryRepository;
        this.mapper = categoryMapper;
        this.productService = productService;
        this.categoryAttributeService = categoryAttributeService;
        this.imageUtils = imageUtils;
    }

    /**
     * Implementors must return their repository.
     */
    @Override
    public JpaRepository<Category, Long> repo() {
        return this.repo;
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
    public Category getById(Long id) {
        return getOrThrow(id);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<Category> search(SearchRequest<CategoryFilter> req) {
        Specification<Category> spec =
                SpecificationFactory.fromRequest(req, CategoryFilter.class);

        Sort sort = new SortResolver(CategoryFilter.class)
                .resolve(req.getSortBy(), req.getSortDirection());

        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 20,
                sort
        );

        return repo.findAll(spec, pageable);
    }


    /**
     * Creates a new root category.
     *
     * @param requestDTO  DTO containing the category details.
     * @return the newly created root category as categoryResponseDTO.
     * @throws IllegalArgumentException   if the name is null or empty.
     * @throws DuplicateResourceException if a root category with the given name already exists.
     */

    @Transactional
    public Category create(CategoryRequestDTO requestDTO) {

        Long parentCategoryId = requestDTO.getParentCategoryId();
        String normalizedCategoryName = normalize(requestDTO.getName());
        String normalizedCategoryDescription = normalize(requestDTO.getDescription());
        Category category;

        if (parentCategoryId == null) {
            validateUniqueRootCategoryName(normalizedCategoryName);
            category = new Category(
                    normalizedCategoryName,
                    normalizedCategoryDescription);
        } else {
            Category parentCategory = getOrThrow(parentCategoryId);

            validateUniqueCategoryNameWithinHierarchy(parentCategory, normalizedCategoryName, null);

            category = new Category(normalizedCategoryName,
                    normalizedCategoryDescription);

            category.setParentCategory(parentCategory);

            parentCategory.getSubCategories().add(category);
        }

        return repo.save(category);
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
    public void delete(Long categoryId) {

        Boolean inUseByCategoryAttributes = categoryAttributeService.existByCategoryId(categoryId);
        Boolean inUseBySubCategories = repo.existsByParentCategoryId(categoryId);
        Boolean inUseByProducts = productService.existsByCategoryId(categoryId);

        if(inUseByCategoryAttributes || inUseBySubCategories || inUseByProducts) {
            throw new CategoryInUseException("Category cannot be deleted because it is in use. Please re-assign or remove dependencies before deleting the category.");
        }

        softDelete(categoryId);
    }


    @Override
    @Transactional
    public Category update(Long id, @Valid CategoryUpdateDTO updateDTO) {

        Category category = getOrThrow(id);

        mapper.updateEntityFromDTO(updateDTO, category);

        return  category;
    }

    /**
     * Uploads or updates the image of a category.
     *
     * @param categoryId the ID of the category.
     * @param image      the MultipartFile representing the image.
     * @return the updated CategoryResponseDTO.
     * @throws ImageProcessingException if validation or reading fails.
     */

    @Transactional
    public Category uploadCategoryImage(Long categoryId, MultipartFile image) {
        Category category = getOrThrow(categoryId);
        byte[] imageBytes = imageUtils.validateAndExtractImageBytes(image);
        category.setImage(imageBytes);
        return category;
    }

    /**
     * Retrieves the image of a category by its ID.
     *
     * @param categoryId the ID of the category whose image is to be retrieved.
     * @return a byte array representing the category image.
     */

    @Transactional(readOnly = true)
    public byte[] getCategoryImage(Long categoryId) {
        Category category = getOrThrow(categoryId);
        byte[] categoryImage = category.getImage();
        if (categoryImage == null) {
            throw new ResourceNotFoundException("Category with ID " + categoryId + " does not have an image.");
        }
        return categoryImage;
    }

    /**
     * Deletes the image of a category by its ID.
     *
     * @param categoryId the ID of the category whose image is to be deleted.
     */

    @Transactional
    public void deleteCategoryImage(Long categoryId) {
        Category category = getOrThrow(categoryId);
        category.setImage(null);
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
     * Validates that a root category name is unique.
     *
     * @param categoryName the name of the root category to validate.
     * @throws DuplicateResourceException if a root category with the given name already exists.
     */

    private void validateUniqueRootCategoryName(String categoryName) {
        if (repo.existsByNameAndParentCategory(categoryName, null)) {
            throw new DuplicateResourceException("Root category with name " + categoryName + " already exists.");
        }
    }
}