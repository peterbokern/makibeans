package com.makibeans.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AttributeTemplate class.
 */

class AttributeTemplateTest {

    private AttributeTemplate attributeTemplate;

    @BeforeEach
    void setUp() {
        attributeTemplate = new AttributeTemplate("Origin");
    }

    @AfterEach
    void tearDown() {
        attributeTemplate = null;
    }

    @Test
    void when_constructed_then_shouldNotBeNull() {
        // Assert
        assertNotNull(attributeTemplate, "AttributeTemplate should not be null after construction");
    }
    
    @Test
    void when_constructedWithName_then_shouldReturnCorrectName() {
        // Act
        String name = attributeTemplate.getName();

        // Assert
        assertEquals("Origin", name, "The name should be set correctly by the constructor");
    }

    @Test
    void when_setName_then_shouldUpdateNameSuccessfully() {
        // Arrange
        String newName = "Flavor";

        // Act
        attributeTemplate.setName(newName);

        // Assert
        assertEquals("Flavor", attributeTemplate.getName(), "The name should be updated correctly via setter");
    }

    @Test
    void when_getName_then_shouldReturnCurrentName() {
        // Act
        String name = attributeTemplate.getName();

        // Assert
        assertEquals("Origin", name, "The name should match the initialized value");
    }

    @Test
    void when_toString_then_shouldReturnExpectedString() {
        // Act
        String result = attributeTemplate.toString();

        // Assert
        String expected = "AttributeTemplate(id=null, name=Origin)";
        assertEquals(expected, result, "The toString method should return the correct string representation");
    }
}
