package com.makibeans.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Size class.
 */
class SizeTest {

    private Size size;

    @BeforeEach
    void setUp() {
        // Arrange
        size = new Size("Large");
    }

    @AfterEach
    void tearDown() {
        size = null;
    }

    // Constructor Tests
    @Test
    void when_created_then_shouldNotBeNull() {
        // Assert
        assertNotNull(size, "Size should not be null after creation");
    }

    @Test
    void when_created_then_shouldSetName() {
        // Assert
        assertEquals("Large", size.getName(), "Size name should be set correctly by constructor");
    }

    // Setter Tests
    @Test
    void when_setName_then_shouldUpdateName() {
        // Act
        size.setName("Medium");

        // Assert
        assertEquals("Medium", size.getName(), "Size name should be updated correctly via setter");
    }

    // Getter Tests
    @Test
    void when_getId_then_shouldReturnNullAsUnpersisted() {
        // Assert
        assertNull(size.getId(), "Size ID should be null before persistence");
    }

    @Test
    void when_getName_then_shouldReturnCorrectName() {
        // Assert
        assertEquals("Large", size.getName(), "Size name should be 'Large' as initialized");
    }

    // toString Method Test
    @Test
    void when_toString_then_shouldReturnCorrectFormat() {
        // Act
        String result = size.toString();

        // Assert
        String expected = "Size(id=null, name=Large)";
        assertEquals(expected, result, "toString() should return the correct string representation");
    }
}
