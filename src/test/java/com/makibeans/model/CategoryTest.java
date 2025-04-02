package com.makibeans.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Category class.
 */

class CategoryTest {

    private Category parentCategory;
    private Category category;

    @BeforeEach
    void setUp() {
        // Arrange
        parentCategory = new Category("Parent Category", "Parent category description");
        category = new Category("Category", "Category description");
        category.setParentCategory(parentCategory);
    }

    @AfterEach
    void tearDown() {
        parentCategory = null;
        category = null;
    }

    // Constructor Tests
    @Test
    void when_constructed_then_shouldNotBeNull() {
        // Assert
        assertNotNull(category, "Category should not be null after construction");
    }

    @Test
    void when_constructed_then_shouldSetName() {
        // Assert
        assertEquals("Category", category.getName(), "Category name should be correctly set by constructor");
    }

    @Test
    void when_constructed_then_shouldSetDescription() {
        // Assert
        assertEquals("Category description", category.getDescription(), "Category description should be correctly set by constructor");
    }

    @Test
    void when_constructed_then_shouldSetParentCategory() {
        // Assert
        assertEquals(parentCategory, category.getParentCategory(), "Parent category should be correctly set by constructor");
    }

    // Setter Tests
    @Test
    void when_setName_then_shouldUpdateName() {
        // Act
        category.setName("New Category");

        // Assert
        assertEquals("New Category", category.getName(), "Category name should be updated via setter");
    }

    @Test
    void when_setDescription_then_shouldUpdateDescription() {
        // Act
        category.setDescription("Updated description");

        // Assert
        assertEquals("Updated description", category.getDescription(), "Category description should be updated via setter");
    }

    @Test
    void when_setImage_then_shouldUpdateImage() {
        // Arrange
        byte[] imageBytes = new byte[]{1, 2, 3};

        // Act
        category.setImage(imageBytes);

        // Assert
        assertArrayEquals(imageBytes, category.getImage(), "Category image should match the set byte array");
    }

    @Test
    void when_setParentCategory_then_shouldUpdateParentCategory() {
        // Arrange
        Category newParent = new Category("New Parent", "New parent description");

        // Act
        category.setParentCategory(newParent);

        // Assert
        assertEquals(newParent, category.getParentCategory(), "Parent category should be updated via setter");
    }

    // toString Method Test (preserved formatting)
    @Test
    void when_toString_then_shouldReturnCorrectFormat() {
        // Act
        String result = category.toString();

        // Assert
        String expected = "Category(id=null, name=Category, description=Category description)";
        assertEquals(expected, result, "toString() should return expected string format");
    }
}
