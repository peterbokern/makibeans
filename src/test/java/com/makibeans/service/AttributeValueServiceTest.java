package com.makibeans.service;

import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.model.AttributeTemplate;
import com.makibeans.model.AttributeValue;
import com.makibeans.repository.AttributeValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class AttributeValueServiceTest {

    private AttributeTemplate attributeTemplate;

    @Mock
    private AttributeValueRepository attributeValueRepository;

    @Mock
    private AttributeTemplateService attributeTemplateService;

    @InjectMocks
    private AttributeValueService attributeValueService;

    @BeforeEach
    void setUp() {
        openMocks(this);
        attributeTemplate = new AttributeTemplate("Origin");
    }

    // Test creating attribute value with valid value
    @Test
    void testCreateAttributeValueWithValidValue() {
        // Arrange
        when(attributeTemplateService.findById(1L)).thenReturn(attributeTemplate);
        when(attributeValueRepository.existsByValue(attributeTemplate, "Chili")).thenReturn(false);
        when(attributeValueRepository.save(any(AttributeValue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AttributeValue result = attributeValueService.createAttributeValue(1L, "Chili");

        // Assert
        assertNotNull(result, "The attribute value should not be null after creation");
        assertEquals(attributeTemplate, result.getAttributeTemplate(), "The attribute template should be correctly associated");
        assertEquals("Chili", result.getValue(), "The value should be 'Chili' as passed in");
        verify(attributeTemplateService).findById(1L);
        verify(attributeValueRepository).existsByValue(any(AttributeTemplate.class), eq("Chili"));
        verify(attributeValueRepository).save(any(AttributeValue.class));
    }

    // Test creating attribute value with invalid value
    @Test
    void testCreateAttributeValueWithInvalidValue() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> attributeValueService.createAttributeValue(1L, null), "Should throw exception for null value");
        assertThrows(IllegalArgumentException.class, () -> attributeValueService.createAttributeValue(null, "Chili"), "Should throw exception for null id");
    }

    // Test creating attribute value with duplicate value
    @Test
    void testCreateAttributeValueWithDuplicateValue() {
        // Arrange
        when(attributeTemplateService.findById(1L)).thenReturn(attributeTemplate);
        when(attributeValueRepository.existsByValue(attributeTemplate, "Chili")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> attributeValueService.createAttributeValue(1L, "Chili"), "Should throw DuplicateResourceException for duplicate value");
        verify(attributeTemplateService).findById(1L);
        verify(attributeValueRepository).existsByValue(any(AttributeTemplate.class), eq("Chili"));
    }

    // Test deleting attribute value
    @Test
    void testDeleteAttributeValue() {
        // Arrange
        AttributeValue attributeValue = new AttributeValue(attributeTemplate, "Chili");
        when(attributeValueRepository.findById(1L)).thenReturn(Optional.of(attributeValue));

        // Act
        attributeValueService.deleteAttributeValue(1L);

        // Assert
        verify(attributeValueRepository).findById(1L);
        verify(attributeValueRepository).delete(attributeValue);
    }

    // Test deleting attribute value with invalid id
    @Test
    void testDeleteAttributeValueInvalidId() {
        //act & assert
        assertThrows(ResourceNotFoundException.class, () -> attributeValueService.deleteAttributeValue(null), "Should throw ResourceNotFoundException for null id");
    }

    // Test deleting attribute value when not found
    @Test
    void testDeleteAttributeValueWhenNotFound() {
        // Arrange
        when(attributeValueRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> attributeValueService.deleteAttributeValue(1L), "Should throw ResourceNotFoundException for non-existent value");
    }

    // Test updating attribute value with valid value
    @Test
    void testUpdateAttributeValueWithValidValue() {
        // Arrange
        AttributeValue attributeValue = new AttributeValue(attributeTemplate, "Chili");
        when(attributeValueRepository.findById(1L)).thenReturn(Optional.of(attributeValue));
        when(attributeValueRepository.save(any(AttributeValue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AttributeValue result = attributeValueService.updateAttributeValue(1L, "Argentina");

        // Assert
        assertNotNull(result, "The result should not be null after update");
        assertEquals("Argentina", result.getValue(), "The value should be 'Argentina' after the update");
        verify(attributeValueRepository).findById(1L);
        verify(attributeValueRepository).save(result);
    }

    // Test updating attribute value with the same value
    @Test
    void testUpdateAttributeValueWithSameValue() {
        // Arrange
        AttributeValue attributeValue = new AttributeValue(attributeTemplate, "Chili");
        when(attributeValueRepository.findById(1L)).thenReturn(Optional.of(attributeValue));

        // Act
        AttributeValue result = attributeValueService.updateAttributeValue(1L, "Chili");

        // Assert
        assertNotNull(result, "The result should not be null");
        assertEquals("Chili", result.getValue(), "The value should be 'Chili' since it hasn't changed");
        verify(attributeValueRepository, never()).save(any(AttributeValue.class)); // Verify that save is not called
    }

    // Test updating attribute value with invalid value
    @Test
    void testUpdateAttributeValueWithInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> attributeValueService.updateAttributeValue(1L, null), "Should throw exception for null value");
        assertThrows(IllegalArgumentException.class, () -> attributeValueService.updateAttributeValue(1L, ""), "Should throw exception for empty value");
    }

    // Test updating attribute value with non-existent id
    @Test
    void testUpdateAttributeValueWithNonExistentId() {
        // Arrange
        when(attributeValueRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> attributeValueService.updateAttributeValue(1L, "Argentina"), "Should throw ResourceNotFoundException for non-existent id");
    }

    // Test finding all attribute values
    @Test
    void testFindAllAttributeValues() {
        // Arrange
        List<AttributeValue> attributeValues = Arrays.asList(new AttributeValue(attributeTemplate, "Chili"), new AttributeValue(attributeTemplate, "Spicy"));
        when(attributeValueRepository.findAll()).thenReturn(attributeValues);

        // Act
        List<AttributeValue> result = attributeValueService.findAll();

        // Assert
        assertNotNull(result, "The result should not be null");
        assertEquals(attributeValues, result, "The retured list of attribute values should match the expected list of attribute values");
        verify(attributeValueRepository).findAll();
    }
}
