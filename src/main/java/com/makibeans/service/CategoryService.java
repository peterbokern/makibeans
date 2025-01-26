package com.makibeans.service;
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

    //add root category
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

    //add subcategory
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
        validateUniqueCategoryNameWithinHierarchy(parentCategory, name);

        //create category
        Category subCategory = new Category(name, description, imageUrl, parentCategory);

        //add as subcategory to parent
        parentCategory.addSubCategory(subCategory);

        //save and return category
        return create(subCategory);
    }

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
                : findById(newParentCategoryId); // Assuming CRUD service's `findById`

        // Check if root category name already exists, excluding the category being updated
        if (newParentCategory == null &&
                !categoryToUpdate.getName().equalsIgnoreCase(newCategoryName) &&
                categoryRepository.existsByNameAndParentCategory(newCategoryName, null)) {
            throw new DuplicateResourceException("Root category with name '" + newCategoryName + "' already exists.");
        }

        // Check for name uniqueness if the name or parent changes
        if (!categoryToUpdate.getName().equalsIgnoreCase(newCategoryName) ||
                (categoryToUpdate.getParentCategory() != newParentCategory)) {
            validateUniqueCategoryNameWithinHierarchy(newParentCategory, newCategoryName);
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


    private void validateCircularReference(Category parentCategory, Category subCategory) {

        Category current = parentCategory;

        while (current != null) {
            if (current.equals(subCategory)) {

                //lekker L1 has lekker as parent now I am trying to put L1 as parent to lekker
                // parentcateogry now is L1
                // subcategory is lekker

                throw new IllegalStateException(String.format("Category %s cannot be a subcategory of %s because this would create a circular reference. Category %s is a (grand)parent of %s.", subCategory.getName(), parentCategory.getName(), current.getName(), parentCategory.getName()));
            }

            current = current.getParentCategory();
        }
    }

    private void validateUniqueCategoryNameWithinHierarchy(Category parentCategory, String categoryName) {

        Category current = parentCategory;

        //check if category already exists under same parent category
        if (parentCategory != null) {
            for (Category sub : parentCategory.getSubCategories()) {
                if (categoryName.equalsIgnoreCase(sub.getName())) {
                    throw new DuplicateResourceException("Category name " + categoryName + " already exists under parent category " + parentCategory.getName() + ".");
                }
            }
        }

        // check if category name exist in hierarchy of parent categories
        while (current != null) {
            if (current.getName().equalsIgnoreCase(categoryName)) {
                throw new DuplicateResourceException(
                        String.format("Category '%s' already exists in the hierarchy of parent categories.", current.getName())
                );
            }
            current = current.getParentCategory(); // Move to the next parent
        }
    }
}
