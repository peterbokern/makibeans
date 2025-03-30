/*
package com.makibeans.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class ProductAttributeTest {

    private ProductAttribute productAttribute;
    private Product product;
    private AttributeTemplate attributeTemplate;
    private AttributeValue attributeValue1;
    private AttributeValue attributeValue2;

    @BeforeEach
    void setUp() {
        product = new Product("Product", "Product description", "product_image_url", new Category("Category", "Category description", "image_url", null));
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
    void shouldReturnNotNullWhenCreated() {
        assertNotNull(productAttribute, "The product attribute should not be null after creation");
    }

    @Test
    void shouldReturnCorrectProductFromConstructor() {
        assertEquals(product, productAttribute.getProduct(), "The product should be correctly initialized by the constructor");
    }

    @Test
    void shouldReturnCorrectAttributeTemplateFromConstructor() {
        assertEquals(attributeTemplate, productAttribute.getAttributeTemplate(), "The attribute template should be correctly initialized by the constructor");
    }

    // Setter Tests
    @Test
    void shouldSetProductSuccessfully() {
        Product newProduct = new Product("New Product", "New Product description", "new_product_image_url", new Category("New Category", "New Category description", "new_image_url", null));
        productAttribute.setProduct(newProduct);
        assertEquals(newProduct, productAttribute.getProduct(), "The product should be set correctly via setter");
    }

    @Test
    void shouldSetAttributeTemplateSuccessfully() {
        AttributeTemplate newTemplate = new AttributeTemplate("Color");
        productAttribute.setAttributeTemplate(newTemplate);
        assertEquals(newTemplate, productAttribute.getAttributeTemplate(), "The attribute template should be set correctly via setter");
    }

    // Relationship Tests
    @Test
    void shouldAddAttributeValueSuccessfully() {
        productAttribute.addAttributeValue(attributeValue1);
        assertTrue(productAttribute.getAttributeValues().contains(attributeValue1), "The attribute values list should contain the added attribute value");
    }

    @Test
    void shouldRemoveAttributeValueSuccessfully() {
        productAttribute.addAttributeValue(attributeValue1);
        productAttribute.removeAttributeValue(attributeValue1);
        assertFalse(productAttribute.getAttributeValues().contains(attributeValue1), "The attribute values list should not contain the removed attribute value");
    }

    // Getter Tests
    @Test
    void shouldReturnCorrectAttributeValuesWhenRequested() {
        productAttribute.addAttributeValue(attributeValue1);
        productAttribute.addAttributeValue(attributeValue2);
        assertEquals(List.of(attributeValue1, attributeValue2), productAttribute.getAttributeValues(), "The attribute values should be correctly retrieved");
    }

    // toString Method Test
    @Test
    void shouldReturnCorrectToString() {
        String expected = "ProductAttribute(id=null, attributeTemplate=AttributeTemplate(id=null, name=Size))";
        assertEquals(expected, productAttribute.toString(), "The toString method should return the correct format");
    }
}
*/
