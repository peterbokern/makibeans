package com.makibeans.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for the Attribute class.
 */

class AttributeTest {

    private Attribute attribute;

    @BeforeEach
    void setUp() {
        attribute = new Attribute("Origin");
    }

    @AfterEach
    void tearDown() {
        attribute = null;
    }

    @Test
    void when_constructed_then_shouldNotBeNull() {
        // Assert
        assertNotNull(attribute, "Attribute should not be null after construction");
    }

    @Test
    void when_constructedWithName_then_shouldReturnCorrectName() {
        // Act
        String name = attribute.getName();

        // Assert
        assertEquals("Origin", name, "The name should be set correctly by the constructor");
    }

    @Test
    void when_setName_then_shouldUpdateNameSuccessfully() {
        // Arrange
        String newName = "Flavor";

        // Act
        attribute.setName(newName);

        // Assert
        assertEquals("Flavor", attribute.getName(), "The name should be updated correctly via setter");
    }

    @Test
    void when_getName_then_shouldReturnCurrentName() {
        // Act
        String name = attribute.getName();

        // Assert
        assertEquals("Origin", name, "The name should match the initialized value");
    }

    @Test
    void when_toString_then_shouldReturnExpectedString() {
        // Act
        String result = attribute.toString();

        // Assert
        String expected = "Attribute(id=null, name=Origin)";
        assertEquals(expected, result, "The toString method should return the correct string representation");
    }
}
