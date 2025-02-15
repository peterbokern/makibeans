package com.makibeans.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SizeTest {

    private Size size;

    @BeforeEach
    void setUp() {
        size = new Size("Large");
        size.setSizeDescription("Large");
    }

    @AfterEach
    void tearDown() {
        size = null;
    }

    // Constructor Tests
    @Test
    void shouldReturnNotNullWhenCreated() {
        assertNotNull(size, "The size object should not be null after creation");
    }

    // Setter Tests
    @Test
    void shouldSetSizeDescriptionSuccessfully() {
        size.setSizeDescription("Medium");
        assertEquals("Medium", size.getSizeDescription(), "The size description should be updated correctly via setter");
    }

    // Getter Tests
    @Test
    void shouldReturnCorrectIdWhenRequested() {
        assertNull(size.getId(), "The id should be null because it's autogenerated and not yet persisted");
    }

    @Test
    void shouldReturnCorrectSizeDescriptionWhenRequested() {
        assertEquals("Large", size.getSizeDescription(), "The size description should be 'Large' as initialized");
    }

    // toString Method Test
    @Test
    void shouldReturnCorrectToString() {
        String expected = "Size(id=null, sizeDescription=Large)";
        assertEquals(expected, size.toString(), "The toString method should return the correct format");
    }
}
