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

        //find parent category
        Category parentCategory = categoryRepository.findById(parentCategoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category with id " + parentCategoryId + " not found."));

        //throw duplicateResourceException if category name already exists within the hierarchy of categories
        validateNameExistsInHierarchy(parentCategory, name);

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
    public Category updateCategory(Long categoryToUpdate, String name, String description, String imageUrl, Long parentCategoryId) {

        if (categoryToUpdate == null) {
            throw new IllegalArgumentException("Category ID should not be null.");
        }

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Category name should not be null or empty.");
        }

        //get category
        Category category = categoryRepository.findById(categoryToUpdate).orElseThrow(() -> new ResourceNotFoundException("Category with id " + categoryToUpdate + " not found."));

        //set new name, description and imageUrl
        category.setName(name);
        category.setDescription(description);
        category.setImageUrl(imageUrl);

        //get current and new parent category
        Category currentParentCategory = category.getParentCategory();

        //set new parent category if provided else set null
        Category newParentCategory = (parentCategoryId == null)
                ? null
                : categoryRepository.findById(parentCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent category with id " + parentCategoryId + " not found"));

        //check that new parent category does not create circular reference if not null
        if (newParentCategory != null) {
            validateCircularReference(newParentCategory, category);
        }

        //set new parent
        category.setParentCategory(newParentCategory);

        //save and return updated category
        return update(categoryToUpdate, category);
    }

    private void validateCircularReference(Category parentCategory, Category subCategory) {

        Category current = parentCategory;

        while (current != null) {
            if (current.equals(subCategory)) {
                throw new IllegalStateException(String.format("Category %s cannot be a subcategory of %s because this would create a circular reference", subCategory.getName(), parentCategory.getName()));
            }

            current = current.getParentCategory();
        }
    }

    private void validateNameExistsInHierarchy(Category parentCategory, String categoryName) {

        Category current = parentCategory;

        // Traverse the hierarchy upwards to check for duplicate names
        while (current != null) {
            if (current.getName().equals(categoryName)) {
                throw new DuplicateResourceException(
                        String.format("Category '%s' already exists in the hierarchy of parent categories.", current.getName())
                );
            }
            current = current.getParentCategory(); // Move to the next parent
        }
    }
}
