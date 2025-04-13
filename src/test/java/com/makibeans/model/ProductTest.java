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
        product = new Product("Product", "Product description", null, category);
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
        assertEquals("Product", product.getName());
    }

    @Test
    void when_created_then_shouldSetDescription() {
        // Assert
        assertEquals("Product description", product.getDescription());
    }

    @Test
    void when_created_then_shouldSetCategory() {
        // Assert
        assertEquals("Category", product.getCategory().getName());
    }

    // Setter Tests
    @Test
    void when_setName_then_shouldUpdateName() {
        // Act
        product.setName("New Product");

        // Assert
        assertEquals("New Product", product.getName());
    }

    @Test
    void when_setDescription_then_shouldUpdateDescription() {
        // Act
        product.setDescription("New Product Description");

        // Assert
        assertEquals("New Product Description", product.getDescription());
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
    void when_setImage_then_shouldUpdateImage() {
        // Arrange
        byte[] image = new byte[]{1, 2, 3};

        // Act
        product.setImage(image);

        // Assert
        assertArrayEquals(image, product.getImage());
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
        assertEquals("Product", product.getName());
    }

    @Test
    void when_getDescription_then_shouldReturnCorrectValue() {
        // Assert
        assertEquals("Product description", product.getDescription());
    }

    @Test
    void when_getCategory_then_shouldReturnCorrectValue() {
        // Assert
        assertEquals("Category", product.getCategory().getName());
    }

    // toString Method Test
    @Test
    void when_toString_then_shouldReturnExpectedFormat() {
        // Act
        String result = product.toString();

        // Assert
        String expected = "Product(id=null, name=Product, description=Product description)";
        assertEquals(expected, result);
    }
}
