package com.makibeans.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ProductVariant class.
 */

class ProductVariantTest {

    private ProductVariant productVariant;
    private Product product;
    private Size size;

    @BeforeEach
    void setUp() {
        // Arrange
        Category category = new Category("Category", "Category description");
        product = new Product("Product", "Product description", "product_image_url", null, category);
        size = new Size("Large");
        productVariant = new ProductVariant(product, size, 1999L, "sku123", 100L);
    }

    @AfterEach
    void tearDown() {
        productVariant = null;
        product = null;
        size = null;
    }

    // Constructor Tests
    @Test
    void when_created_then_shouldNotBeNull() {
        // Assert
        assertNotNull(productVariant, "ProductVariant should not be null after creation");
    }

    @Test
    void when_created_then_shouldSetProduct() {
        // Assert
        assertEquals(product, productVariant.getProduct(), "Product should be set correctly in constructor");
    }

    @Test
    void when_created_then_shouldSetSize() {
        // Assert
        assertEquals(size, productVariant.getSize(), "Size should be set correctly in constructor");
    }

    @Test
    void when_created_then_shouldSetPrice() {
        // Assert
        assertEquals(1999L, productVariant.getPriceInCents(), "Price should be set correctly in constructor");
    }

    @Test
    void when_created_then_shouldSetSku() {
        // Assert
        assertEquals("sku123", productVariant.getSku(), "SKU should be set correctly in constructor");
    }

    @Test
    void when_created_then_shouldSetStock() {
        // Assert
        assertEquals(100L, productVariant.getStock(), "Stock should be set correctly in constructor");
    }

    // Setter Tests
    @Test
    void when_setProduct_then_shouldUpdateProduct() {
        // Arrange
        Category newCategory = new Category("New Category", "New description");
        Product newProduct = new Product("New Product", "New description", "new_image_url", null, newCategory);

        // Act
        productVariant.setProduct(newProduct);

        // Assert
        assertEquals(newProduct, productVariant.getProduct(), "Product should be updated via setter");
    }

    @Test
    void when_setSize_then_shouldUpdateSize() {
        // Arrange
        Size newSize = new Size("Medium");

        // Act
        productVariant.setSize(newSize);

        // Assert
        assertEquals(newSize, productVariant.getSize(), "Size should be updated via setter");
    }

    @Test
    void when_setPrice_then_shouldUpdatePrice() {
        // Act
        productVariant.setPriceInCents(2999L);

        // Assert
        assertEquals(2999L, productVariant.getPriceInCents(), "Price should be updated via setter");
    }

    @Test
    void when_setSku_then_shouldUpdateSku() {
        // Act
        productVariant.setSku("new_sku");

        // Assert
        assertEquals("new_sku", productVariant.getSku(), "SKU should be updated via setter");
    }

    @Test
    void when_setStock_then_shouldUpdateStock() {
        // Act
        productVariant.setStock(150L);

        // Assert
        assertEquals(150L, productVariant.getStock(), "Stock should be updated via setter");
    }

    // Getter Tests
    @Test
    void when_getProduct_then_shouldReturnCorrectProduct() {
        // Assert
        assertEquals(product, productVariant.getProduct(), "getProduct should return the initialized product");
    }

    @Test
    void when_getSize_then_shouldReturnCorrectSize() {
        // Assert
        assertEquals(size, productVariant.getSize(), "getSize should return the initialized size");
    }

    @Test
    void when_getPrice_then_shouldReturnCorrectPrice() {
        // Assert
        assertEquals(1999L, productVariant.getPriceInCents(), "getPriceInCents should return the initialized price");
    }

    @Test
    void when_getSku_then_shouldReturnCorrectSku() {
        // Assert
        assertEquals("sku123", productVariant.getSku(), "getSku should return the initialized SKU");
    }

    @Test
    void when_getStock_then_shouldReturnCorrectStock() {
        // Assert
        assertEquals(100L, productVariant.getStock(), "getStock should return the initialized stock");
    }

    // toString Method Test
    @Test
    void when_toString_then_shouldReturnCorrectFormat() {
        // Act
        String result = productVariant.toString();

        // Assert
        String expected = "ProductVariant(id=null, priceInCents=1999, sku=sku123, stock=100)";
        assertEquals(expected, result, "toString() should return the expected string format");
    }
}
