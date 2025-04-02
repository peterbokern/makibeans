package com.makibeans.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for the ProductAttribute class.
 */

class ProductAttributeTest {

    private ProductAttribute productAttribute;
    private Product product;
    private AttributeTemplate attributeTemplate;
    private AttributeValue attributeValue1;
    private AttributeValue attributeValue2;

    @BeforeEach
    void setUp() {
        // Arrange
        Category category = new Category("Category", "Category description");
        product = new Product("Product", "Product description", "product_image_url", null, category);
        attributeTemplate = new AttributeTemplate("Size");
        productAttribute = new ProductAttribute(attributeTemplate, product);

        attributeValue1 = new AttributeValue(attributeTemplate, "Large");
        attributeValue2 = new AttributeValue(attributeTemplate, "Small");
    }

    @AfterEach
    void tearDown() {
        productAttribute = null;
        product = null;
        attributeTemplate = null;
        attributeValue1 = null;
        attributeValue2 = null;
    }

    // Constructor Tests
    @Test
    void when_created_then_shouldNotBeNull() {
        // Assert
        assertNotNull(productAttribute, "ProductAttribute should not be null after construction");
    }

    @Test
    void when_created_then_shouldSetProduct() {
        // Assert
        assertEquals(product, productAttribute.getProduct(), "Product should be correctly set via constructor");
    }

    @Test
    void when_created_then_shouldSetAttributeTemplate() {
        // Assert
        assertEquals(attributeTemplate, productAttribute.getAttributeTemplate(), "AttributeTemplate should be correctly set via constructor");
    }

    // Setter Tests
    @Test
    void when_setProduct_then_shouldUpdateProduct() {
        // Arrange
        Product newProduct = new Product("New Product", "New Product description", "new_product_image_url", null, new Category("New Category", "New Description"));

        // Act
        productAttribute.setProduct(newProduct);

        // Assert
        assertEquals(newProduct, productAttribute.getProduct(), "Product should be updated by setter");
    }

    @Test
    void when_setAttributeTemplate_then_shouldUpdateTemplate() {
        // Arrange
        AttributeTemplate newTemplate = new AttributeTemplate("Color");

        // Act
        productAttribute.setAttributeTemplate(newTemplate);

        // Assert
        assertEquals(newTemplate, productAttribute.getAttributeTemplate(), "AttributeTemplate should be updated by setter");
    }

    // Getter Tests
    @Test
    void when_getAttributeValues_then_shouldReturnCorrectList() {
        // Arrange
        productAttribute.getAttributeValues().add(attributeValue1);
        productAttribute.getAttributeValues().add(attributeValue2);

        // Assert
        assertEquals(List.of(attributeValue1, attributeValue2), productAttribute.getAttributeValues(), "Attribute values list should contain both added values");
    }

    @Test
    void when_getId_then_shouldReturnNullAsUnpersisted() {
        // Assert
        assertNull(productAttribute.getId(), "ID should be null before persistence");
    }

    // toString Method Test
    @Test
    void when_toString_then_shouldReturnExpectedFormat() {
        // Act
        String result = productAttribute.toString();

        // Assert
        String expected = "ProductAttribute(id=null, attributeTemplate=AttributeTemplate(id=null, name=Size))";
        assertEquals(expected, result, "toString() output should match expected format");
    }
}
