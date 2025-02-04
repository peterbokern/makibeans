package com.makibeans.service;
import com.makibeans.exeptions.CircularReferenceException;
import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.model.Category;
import com.makibeans.model.Product;
import com.makibeans.repository.AttributeValueRepository;
import com.makibeans.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService extends AbstractCrudService<Category, Long> {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(JpaRepository<Category, Long> repository, CategoryRepository categoryRepository, AttributeValueRepository attributeValueRepository) {
        super(repository);
        this.categoryRepository = categoryRepository;
    }

    /**
     * Creates a new root category.
     *
     * @param name        the name of the category; must not be null or empty.
     * @param description the description of the category.
     * @param imageUrl    the image URL for the category.
     * @return the newly created root category.
     * @throws IllegalArgumentException      if the name is null or empty.
     * @throws DuplicateResourceException   if a root category with the given name already exists.
     */

    @Transactional
    public Category createRootCategory(String name, String description, String imageUrl) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }

        //check if root category name already exists
        if (categoryRepository.existsByNameAndParentCategory(name, null)) {
            throw new DuplicateResourceException("Root category with name  " + name + " already exists.");
        }

        //create category
        Category category = new Category(name, description, imageUrl);

        //save category
        return create(category);
    }

    /**
     * Creates a new subcategory under a given parent category.
     *
     * @param name the name of the subcategory; must not be null or empty.
     * @param description the description of the subcategory.
     * @param imageUrl the image URL for the subcategory.
     * @param parentCategoryId the ID of the parent category; must not be null.
     * @return the newly created subcategory.
     * @throws IllegalArgumentException if the name or parentCategoryId is null or empty.
     * @throws ResourceNotFoundException if the parent category does not exist.
     * @throws DuplicateResourceException if a category with the same name already exists within the hierarchy.
     */

    @Transactional
    public Category createSubCategory(String name, String description, String imageUrl, Long parentCategoryId) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }

        if (parentCategoryId == null) {
            throw new IllegalArgumentException("Parent category id cannot be null for a subcategory.");
        }

        //find parent category
        Category parentCategory = categoryRepository.findById(parentCategoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category with id " + parentCategoryId + " not found."));

        //throw duplicateResourceException if category name already exists within the hierarchy of categories
        validateUniqueCategoryNameWithinHierarchy(parentCategory, name, null); //CHECK

        //create category
        Category subCategory = new Category(name, description, imageUrl, parentCategory);

        //add as subcategory to parent
        parentCategory.addSubCategory(subCategory);

        //save and return category
        return create(subCategory);
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

        if (categoryId == null) {
            throw new IllegalArgumentException("Category id cannot be null.");
        }

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
     * @param categoryToUpdateId the ID of the category to update; must not be null.
     * @param newCategoryName the new name of the category; must not be null or empty.
     * @param newCategoryDescription the new description of the category.
     * @param newImageUrl the new image URL of the category.
     * @param newParentCategoryId  the ID of the new parent category, or null for a root category.
     * @return the updated category.
     * @throws IllegalArgumentException if categoryToUpdateId or newCategoryName is null or empty.
     * @throws ResourceNotFoundException if the category or new parent category does not exist.
     * @throws DuplicateResourceException if a category with the same name already exists in the new hierarchy.
     * @throws CircularReferenceException if the new parent category creates a circular reference.

     */

    @Transactional
    public Category updateCategory(Long categoryToUpdateId, String newCategoryName, String newCategoryDescription, String newImageUrl, Long newParentCategoryId) {

        if (categoryToUpdateId == null) {
            throw new IllegalArgumentException("Category ID should not be null.");
        }

        if (newCategoryName == null || newCategoryName.isEmpty()) {
            throw new IllegalArgumentException("Category name should not be null or empty.");
        }

        // Fetch the category to update
        Category categoryToUpdate = findById(categoryToUpdateId);

        // Fetch the new parent category if provided
        Category newParentCategory = (newParentCategoryId == null)
                ? null
                : findById(newParentCategoryId);

        // Check if root category name already exists, excluding the category being updated
        if (newParentCategory == null &&
                !categoryToUpdate.getName().equalsIgnoreCase(newCategoryName) &&
                categoryRepository.existsByNameAndParentCategory(newCategoryName, null)) {
            throw new DuplicateResourceException("Root category with name '" + newCategoryName + "' already exists.");
        }

        // Check for name uniqueness if the name or parent changes
        if (!categoryToUpdate.getName().equalsIgnoreCase(newCategoryName) ||
                (categoryToUpdate.getParentCategory() != newParentCategory)) {

            validateUniqueCategoryNameWithinHierarchy(newParentCategory, newCategoryName, categoryToUpdate);
        }

        // Validate circular references if the parent changes
        if (newParentCategory != null) {
            validateCircularReference(newParentCategory, categoryToUpdate);
        }

        // Update fields
        categoryToUpdate.setName(newCategoryName);
        categoryToUpdate.setDescription(newCategoryDescription);
        categoryToUpdate.setImageUrl(newImageUrl);
        categoryToUpdate.setParentCategory(newParentCategory);

        // Use CRUD service's `update`
        return update(categoryToUpdateId, categoryToUpdate);
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
                        "Category %s cannot be a subcategory of %s because this would create a circular reference. Category %s is a (grand)parent of %s.",
                        subCategory.getName(), parentCategory.getName(), current.getName(), parentCategory.getName()));
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
