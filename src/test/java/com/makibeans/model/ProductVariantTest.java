package com.makibeans.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductVariantTest {

    private ProductVariant productVariant;
    private Product product;
    private Size size;

    @BeforeEach
    void setUp() {
        product = new Product("Product", "Product description", "product_image_url", new Category("Category", "Category description", "image_url", null));
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
    void shouldReturnNotNullWhenCreated() {
        assertNotNull(productVariant, "The product variant should not be null after creation");
    }

    @Test
    void shouldReturnCorrectProductFromConstructor() {
        assertEquals(product, productVariant.getProduct(), "The product should be correctly initialized by the constructor");
    }

    @Test
    void shouldReturnCorrectSizeFromConstructor() {
        assertEquals(size, productVariant.getSize(), "The size should be correctly initialized by the constructor");
    }

    @Test
    void shouldReturnCorrectPriceFromConstructor() {
        assertEquals(1999L, productVariant.getPriceInCents(), "The price should be correctly initialized by the constructor");
    }

    @Test
    void shouldReturnCorrectSkuFromConstructor() {
        assertEquals("sku123", productVariant.getSku(), "The SKU should be correctly initialized by the constructor");
    }

    @Test
    void shouldReturnCorrectStockFromConstructor() {
        assertEquals(100L, productVariant.getStock(), "The stock should be correctly initialized by the constructor");
    }

    // Setter Tests
    @Test
    void shouldSetProductSuccessfully() {
        Product newProduct = new Product("New Product", "New description", "new_image_url", new Category("New Category", "Description", "new_image_url", null));
        productVariant.setProduct(newProduct);
        assertEquals(newProduct, productVariant.getProduct(), "The product should be set correctly via setter");
    }

    @Test
    void shouldSetSizeSuccessfully() {
        Size newSize = new Size("Medium");
        productVariant.setSize(newSize);
        assertEquals(newSize, productVariant.getSize(), "The size should be set correctly via setter");
    }

    @Test
    void shouldSetPriceSuccessfully() {
        productVariant.setPriceInCents(2999L);
        assertEquals(2999L, productVariant.getPriceInCents(), "The price should be set correctly via setter");
    }

    @Test
    void shouldSetSkuSuccessfully() {
        productVariant.setSku("new_sku");
        assertEquals("new_sku", productVariant.getSku(), "The SKU should be set correctly via setter");
    }

    @Test
    void shouldSetStockSuccessfully() {
        productVariant.setStock(150L);
        assertEquals(150L, productVariant.getStock(), "The stock should be set correctly via setter");
    }

    // Getter Tests
    @Test
    void shouldReturnCorrectProductWhenRequested() {
        assertEquals(product, productVariant.getProduct(), "The product should be 'Product' as initialized");
    }

    @Test
    void shouldReturnCorrectSizeWhenRequested() {
        assertEquals(size, productVariant.getSize(), "The size should be 'Large' as initialized");
    }

    @Test
    void shouldReturnCorrectPriceWhenRequested() {
        assertEquals(1999L, productVariant.getPriceInCents(), "The price should be '1999' as initialized");
    }

    @Test
    void shouldReturnCorrectSkuWhenRequested() {
        assertEquals("sku123", productVariant.getSku(), "The SKU should be 'sku123' as initialized");
    }

    @Test
    void shouldReturnCorrectStockWhenRequested() {
        assertEquals(100L, productVariant.getStock(), "The stock should be '100' as initialized");
    }

    // toString Method Test
    @Test
    void shouldReturnCorrectToString() {
        String expected = "ProductVariant{id=null, product=" + product + ", size=" + size + ", priceInCents=1999, sku='sku123', stock=100}";
        assertEquals(expected, productVariant.toString(), "The toString method should return the correct format");
    }
}
