package com.makibeans.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    private Category parentCategory;
    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        // Create a parent category
        parentCategory = new Category("Parent Category", "Parent category description", "image_url", null);
        // Create a category with the parent
        category = new Category("Category", "Category description", "image_url", parentCategory);
        // Create a product
        product = new Product("Product", "Product description", "product_image_url", category);
    }

    @AfterEach
    void tearDown() {
        parentCategory = null;
        category = null;
        product = null;
    }

    // Constructor Tests
    @Test
    void shouldReturnCategoryNameFromConstructor() {
        assertEquals("Category", category.getName(), "Category name should be initialized correctly from constructor");
    }

    @Test
    void shouldReturnCategoryDescriptionFromConstructor() {
        assertEquals("Category description", category.getDescription(), "Category description should be initialized correctly from constructor");
    }

    @Test
    void shouldReturnCategoryImageUrlFromConstructor() {
        assertEquals("image_url", category.getImageUrl(), "Category image URL should be initialized correctly from constructor");
    }

    @Test
    void shouldReturnParentCategoryFromConstructor() {
        assertEquals(parentCategory, category.getParentCategory(), "Category's parent category should be initialized correctly from constructor");
    }

    // Setter Tests
    @Test
    void shouldSetCategoryNameSuccessfully() {
        category.setName("New Category");
        assertEquals("New Category", category.getName(), "Category name should be updated correctly via setter");
    }

    @Test
    void shouldSetCategoryDescriptionSuccessfully() {
        category.setDescription("Updated description");
        assertEquals("Updated description", category.getDescription(), "Category description should be updated correctly via setter");
    }

    @Test
    void shouldSetCategoryImageUrlSuccessfully() {
        category.setImageUrl("new_image_url");
        assertEquals("new_image_url", category.getImageUrl(), "Category image URL should be updated correctly via setter");
    }

    @Test
    void shouldSetParentCategorySuccessfully() {
        Category newParentCategory = new Category("New Parent Category", "New parent category description", "new_image_url", null);
        category.setParentCategory(newParentCategory);
        assertEquals(newParentCategory, category.getParentCategory(), "Category's parent category should be updated correctly via setter");
    }

    // Getter Tests
    @Test
    void shouldReturnCategoryNameWhenRequested() {
        assertEquals("Category", category.getName(), "Category name should be returned correctly by getter");
    }

    @Test
    void shouldReturnCategoryDescriptionWhenRequested() {
        assertEquals("Category description", category.getDescription(), "Category description should be returned correctly by getter");
    }

    @Test
    void shouldReturnCategoryImageUrlWhenRequested() {
        assertEquals("image_url", category.getImageUrl(), "Category image URL should be returned correctly by getter");
    }

    @Test
    void shouldReturnParentCategoryWhenRequested() {
        assertEquals(parentCategory, category.getParentCategory(), "Category's parent category should be returned correctly by getter");
    }

    // Relationship Tests (Add/Remove Product)
    @Test
    void shouldAddProductSuccessfully() {
        category.addProduct(product);
        assertTrue(category.getProducts().contains(product), "Product should be added to the category");
        assertEquals(category, product.getCategory(), "Product's category should be updated correctly");
    }

    @Test
    void shouldRemoveProductSuccessfully() {
        category.addProduct(product);
        category.removeProduct(product);
        assertFalse(category.getProducts().contains(product), "Product should be removed from the category");
        assertNull(product.getCategory(), "Product's category should be set to null after removal");
    }

    // Relationship Tests (Add/Remove SubCategory)
    @Test
    void shouldAddSubCategorySuccessfully() {
        Category subCategory = new Category("SubCategory", "SubCategory description", "sub_image_url", category);
        category.addSubCategory(subCategory);
        assertTrue(category.getSubCategories().contains(subCategory), "Subcategory should be added to the category");
        assertEquals(category, subCategory.getParentCategory(), "Subcategory's parent category should be updated correctly");
    }

    @Test
    void shouldRemoveSubCategorySuccessfully() {
        Category subCategory = new Category("SubCategory", "SubCategory description", "sub_image_url", category);
        category.addSubCategory(subCategory);
        category.removeSubCategory(subCategory);
        assertFalse(category.getSubCategories().contains(subCategory), "Subcategory should be removed from the category");
        assertNull(subCategory.getParentCategory(), "Subcategory's parent category should be set to null after removal");
    }

    // toString Method Test
    @Test
    void shouldReturnCorrectToString() {
        String expected = "Category(id=null, name=Category, description=Category description, imageUrl=image_url)";
        assertEquals(expected, category.toString(), "toString() should return the correct string representation of the Category object");
    }
}
