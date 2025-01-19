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

    @Test
    void shouldAddProductSuccessfully() {
        category.addProduct(product);
        assertTrue(category.getProducts().contains(product));
        assertEquals(category, product.getCategory());
    }

    @Test
    void shouldRemoveProductSuccessfully() {
        category.addProduct(product);
        category.removeProduct(product);
        assertFalse(category.getProducts().contains(product));
        assertNull(product.getCategory());
    }

    @Test
    void shouldAddSubCategorySuccessfully() {
        Category subCategory = new Category("SubCategory", "SubCategory description", "sub_image_url", category);
        category.addSubCategory(subCategory);
        assertTrue(category.getSubCategories().contains(subCategory));
        assertEquals(category, subCategory.getParentCategory());
    }

    @Test
    void shouldRemoveSubCategorySuccessfully() {
        Category subCategory = new Category("SubCategory", "SubCategory description", "sub_image_url", category);
        category.addSubCategory(subCategory);
        category.removeSubCategory(subCategory);
        assertFalse(category.getSubCategories().contains(subCategory));
        assertNull(subCategory.getParentCategory());
    }

    @Test
    void shouldReturnCorrectToString() {
        String expected = "Category{id=null, name='Category', description='Category description', imageUrl='image_url', parentCategory=Parent Category, subCategories=[], products=[]}";
        assertEquals(expected, category.toString());
    }
}
