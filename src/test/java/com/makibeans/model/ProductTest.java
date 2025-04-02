package com.makibeans.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Product class.
 */

class ProductTest {

    private Product product;
    private ProductVariant variant;
    private ProductVariant variant2;
    private ProductAttribute attribute1;
    private ProductAttribute attribute2;

    @BeforeEach
    void setUp() {
        // Arrange
        Category category = new Category("Category", "Category description");
        product = new Product("Product", "Product description", "product_image_url", null, category);
        variant = new ProductVariant(product, new Size("Large"), 10L, "sku", 10L);
        variant2 = new ProductVariant(product, new Size("Small"), 10L, "sku", 10L);
        attribute1 = new ProductAttribute(new AttributeTemplate("Origin"), product);
        attribute2 = new ProductAttribute(new AttributeTemplate("Color"), product);
    }

    @AfterEach
    void tearDown() {
        product = null;
        variant = null;
        variant2 = null;
        attribute1 = null;
        attribute2 = null;
    }

    // Constructor Tests
    @Test
    void when_created_then_shouldNotBeNull() {
        // Assert
        assertNotNull(product);
    }

    @Test
    void when_created_then_shouldSetName() {
        // Assert
        assertEquals("Product", product.getProductName());
    }

    @Test
    void when_created_then_shouldSetDescription() {
        // Assert
        assertEquals("Product description", product.getProductDescription());
    }

    @Test
    void when_created_then_shouldSetCategory() {
        // Assert
        assertEquals("Category", product.getCategory().getName());
    }

    @Test
    void when_created_then_shouldSetImageUrl() {
        // Assert
        assertEquals("product_image_url", product.getProductImageUrl());
    }

    // Setter Tests
    @Test
    void when_setName_then_shouldUpdateName() {
        // Act
        product.setProductName("New Product");

        // Assert
        assertEquals("New Product", product.getProductName());
    }

    @Test
    void when_setDescription_then_shouldUpdateDescription() {
        // Act
        product.setProductDescription("New Product Description");

        // Assert
        assertEquals("New Product Description", product.getProductDescription());
    }

    @Test
    void when_setCategory_then_shouldUpdateCategory() {
        // Arrange
        Category newCategory = new Category("New Category", "New Category description");

        // Act
        product.setCategory(newCategory);

        // Assert
        assertEquals("New Category", product.getCategory().getName());
    }

    @Test
    void when_setImageUrl_then_shouldUpdateImageUrl() {
        // Act
        product.setProductImageUrl("new_product_image_url");

        // Assert
        assertEquals("new_product_image_url", product.getProductImageUrl());
    }

    @Test
    void when_setImage_then_shouldUpdateImage() {
        // Arrange
        byte[] image = new byte[]{1, 2, 3};

        // Act
        product.setProductImage(image);

        // Assert
        assertArrayEquals(image, product.getProductImage());
    }

    // Getter Tests
    @Test
    void when_getId_then_shouldReturnNullAsUnpersisted() {
        // Assert
        assertNull(product.getId());
    }

    @Test
    void when_getName_then_shouldReturnCorrectValue() {
        // Assert
        assertEquals("Product", product.getProductName());
    }

    @Test
    void when_getDescription_then_shouldReturnCorrectValue() {
        // Assert
        assertEquals("Product description", product.getProductDescription());
    }

    @Test
    void when_getCategory_then_shouldReturnCorrectValue() {
        // Assert
        assertEquals("Category", product.getCategory().getName());
    }

    @Test
    void when_getImageUrl_then_shouldReturnCorrectValue() {
        // Assert
        assertEquals("product_image_url", product.getProductImageUrl());
    }

    // toString Method Test
    @Test
    void when_toString_then_shouldReturnExpectedFormat() {
        // Act
        String result = product.toString();

        // Assert
        String expected = "Product(id=null, productName=Product, productDescription=Product description, productImageUrl=product_image_url)";
        assertEquals(expected, result);
    }
}
