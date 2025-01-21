package com.makibeans.service;

import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.model.AttributeTemplate;
import com.makibeans.repository.AttributeTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class AttributeTemplateServiceTest {

    @Mock
    private AttributeTemplateRepository attributeTemplateRepository;

    @InjectMocks
    private AttributeTemplateService attributeTemplateService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testCreateAttributeTemplateWithValidName() {
        // Arrange: Mock repository behavior
        when(attributeTemplateRepository.existsByName(any(String.class))).thenReturn(false);
        when(attributeTemplateRepository.save(any(AttributeTemplate.class)))
                .thenAnswer(invocation -> {
                            AttributeTemplate result = invocation.getArgument(0);
                            return new AttributeTemplate(result.getName());
                        });

        // Act: Call the method under test
        AttributeTemplate result = attributeTemplateService.createAttributeTemplate("Origin");

        // Assert: Verify the result
        assertNotNull(result, "The attribute template should not be null after creation");
        assertEquals("Origin", result.getName(), "The attribute template name should be correctly initialized");

        // Verify: Check interactions with the repository
        verify(attributeTemplateRepository).existsByName("Origin");
        verify(attributeTemplateRepository).save(any(AttributeTemplate.class));
    }

    @Test
    void testCreateAttributeTemplateWithInvalidName() {
        // Arrange: Nothing to mock

        // Act & Assert: Check for IllegalArgumentException on invalid names
        assertThrows(IllegalArgumentException.class, () -> attributeTemplateService.createAttributeTemplate(null));
        assertThrows(IllegalArgumentException.class, () -> attributeTemplateService.createAttributeTemplate(""));
    }

    @Test
    void testCreateAttributeTemplateWithDuplicateName() {
        // Arrange: Mock repository behavior for duplicate name
        when(attributeTemplateRepository.existsByName(any(String.class))).thenReturn(true);

        // Act & Assert: Ensure DuplicateResourceException is thrown for duplicate names
        assertThrows(DuplicateResourceException.class, () -> attributeTemplateService.createAttributeTemplate("Origin"));
    }

    @Test
    void testDeleteAttributeTemplate() {
        // Arrange: Mock findById to return an existing template
        when(attributeTemplateRepository.findById(any(Long.class))).thenReturn(Optional.of(new AttributeTemplate("Origin")));

        // Act: Call the delete method
        attributeTemplateService.deleteAttributeTemplate(1L);

        // Assert: Verify delete interaction with the repository
        verify(attributeTemplateRepository).delete(any(AttributeTemplate.class));
    }

    @Test
    void testDeleteAttributeTemplateInvalidId() {
        // Arrange: Mock findById to return empty for invalid ID
        when(attributeTemplateRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        // Act & Assert: Check if ResourceNotFoundException is thrown for invalid ID
        assertThrows(ResourceNotFoundException.class, () -> attributeTemplateService.deleteAttributeTemplate(1L));
    }

    @Test
    void testUpdateAttributeTemplateWithValidName() {
        // Arrange: Mock repository behavior for findById and save
        when(attributeTemplateRepository.findById(any(Long.class))).thenReturn(Optional.of(new AttributeTemplate("Origin")));
        when(attributeTemplateRepository.save(any(AttributeTemplate.class)))
                .thenAnswer(invocation -> {
                    AttributeTemplate result = invocation.getArgument(0);
                    return new AttributeTemplate(result.getName());
                });

        // Act: Call update method
        AttributeTemplate result = attributeTemplateService.updateAttributeTemplate(1L, "Flavor");

        // Assert: Verify the result and repository interactions
        assertNotNull(result, "The attribute template should not be null after update");
        assertEquals("Flavor", result.getName(), "The attribute template name should be correctly updated");
        verify(attributeTemplateRepository).findById(any(long.class));
        verify(attributeTemplateRepository).save(any(AttributeTemplate.class));
    }

    @Test
    void testFindById() {
        // Arrange: Mock repository to return an existing template
        when(attributeTemplateRepository.findById(any(Long.class))).thenReturn(Optional.of(new AttributeTemplate("Origin")));

        // Act: Call findById
        AttributeTemplate result = attributeTemplateService.findById(1L);

        // Assert: Verify the result and repository interaction
        assertNotNull(result, "The attribute template should not be null");
        assertEquals("Origin", result.getName(), "The attribute template name should be 'Origin'");
        verify(attributeTemplateRepository).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        // Arrange: Mock repository to return empty for a non-existent template
        when(attributeTemplateRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        // Act & Assert: Check if ResourceNotFoundException is thrown
        assertThrows(ResourceNotFoundException.class, () -> attributeTemplateService.findById(1L));
    }

    @Test
    void testFindAll() {
        // Arrange: Mock repository to return a list of templates
        List<AttributeTemplate> templates = Arrays.asList(new AttributeTemplate("Origin"), new AttributeTemplate("Size"));
        when(attributeTemplateRepository.findAll()).thenReturn(templates);

        // Act: Call findAll
        List<AttributeTemplate> result = attributeTemplateService.findAll();

        // Assert: Verify the result and repository interaction
        assertNotNull(result, "The result should not be null");
        assertEquals(2, result.size(), "The result size should be 2");
        verify(attributeTemplateRepository).findAll();
    }
}