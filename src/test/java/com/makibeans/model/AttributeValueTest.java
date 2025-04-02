package com.makibeans.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttributeValueTest {

    private AttributeValue attributeValue;

    @BeforeEach
    void setUp() {
        attributeValue = new AttributeValue(new AttributeTemplate("Size"), "Large");
    }

    @AfterEach
    void tearDown() {
        attributeValue = null;
    }

    @Test
    void when_created_then_shouldNotBeNull() {
        // Assert
        assertNotNull(attributeValue, "The attribute value should not be null after creation");
    }

    @Test
    void when_constructed_then_shouldReturnCorrectTemplateName() {
        // Act
        String templateName = attributeValue.getAttributeTemplate().getName();

        // Assert
        assertEquals("Size", templateName, "The attribute template name should be correctly initialized by the constructor");
    }

    @Test
    void when_constructed_then_shouldReturnCorrectValue() {
        // Act
        String value = attributeValue.getValue();

        // Assert
        assertEquals("Large", value, "The value should be correctly initialized by the constructor");
    }

    @Test
    void when_setValue_then_shouldUpdateSuccessfully() {
        // Arrange
        String newValue = "Small";

        // Act
        attributeValue.setValue(newValue);

        // Assert
        assertEquals("Small", attributeValue.getValue(), "The value should be updated correctly via setter");
    }

    @Test
    void when_toString_then_shouldNotIncludeAttributeTemplate() {
        // Act
        String result = attributeValue.toString();

        // Assert
        assertTrue(result.contains("value=Large"), "toString should include value");
        assertFalse(result.contains("attributeTemplate"), "toString should exclude attributeTemplate");
    }
}
