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
    void shouldReturnNotNullWhenCreated() {
        assertNotNull(attributeValue);
    }

    @Test
    void shouldReturnCorrectAttributeTemplateFromConstructor() {
        assertEquals("Size", attributeValue.getAttributeTemplate().getName());
    }

    @Test
    void shouldReturnCorrectValueFromConstructor() {
        assertEquals("Large", attributeValue.getValue());
    }

    @Test
    void shouldSetAttributeValueSuccessfully() {
        attributeValue.setValue("Small");
        assertEquals("Small", attributeValue.getValue());
    }

    @Test
    void shouldReturnCorrectToString() {
        String expected = "AttributeValue{attributeTemplate=Size, id=null, value='Large'}";
        assertEquals(expected, attributeValue.toString());
    }

}