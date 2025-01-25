package com.makibeans.service;

import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.model.Category;
import com.makibeans.repository.AttributeValueRepository;
import com.makibeans.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService extends AbstractCrudService<Category, Long> {

    private final CategoryRepository categoryRepository;
    private final AttributeValueRepository attributeValueRepository;

    @Autowired
    public CategoryService(JpaRepository<Category, Long> repository, CategoryRepository categoryRepository, AttributeValueRepository attributeValueRepository) {
        super(repository);
        this.categoryRepository = categoryRepository;
        this.attributeValueRepository = attributeValueRepository;
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



    public void deleteCategory() {
    }

    /*public updateCategory() {
    }*/

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
