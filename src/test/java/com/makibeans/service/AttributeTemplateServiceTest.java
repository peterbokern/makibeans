package com.makibeans.service;

import com.makibeans.dto.AttributeTemplateDTO;
import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.model.AttributeTemplate;
import com.makibeans.repository.AttributeTemplateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttributeTemplateServiceTest {

    private AttributeTemplateDTO dto;

    @Mock
    private AttributeTemplateRepository attributeTemplateRepository;

    @InjectMocks
    private AttributeTemplateService attributeTemplateService;

    @BeforeEach
    void setUp() {
        dto = new AttributeTemplateDTO();
        dto.setName("  Origin  "); // Simulate untrimmed input
    }

    @AfterEach
    void tearDown() {
        dto = null;
    }

    @Test
    void should_CreateAttributeTemplate_When_ValidNameProvided() {
        // Arrange
        when(attributeTemplateRepository.existsByName("origin")).thenReturn(false);
        when(attributeTemplateRepository.save(any(AttributeTemplate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AttributeTemplate result = attributeTemplateService.createAttributeTemplate(dto);

        // Assert
        assertNotNull(result);
        assertEquals("origin", result.getName(), "The attribute template name should be normalized");

        // Verify
        verify(attributeTemplateRepository).existsByName(eq("origin"));
        verify(attributeTemplateRepository).save(any(AttributeTemplate.class));
    }

    @Test
    void should_ThrowDuplicateResourceException_When_NameAlreadyExists() {
        // Arrange
        when(attributeTemplateRepository.existsByName("origin")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class,
                () -> attributeTemplateService.createAttributeTemplate(dto),
                "Expected exception when name already exists");

        verify(attributeTemplateRepository).existsByName(eq("origin"));
        verifyNoMoreInteractions(attributeTemplateRepository);
    }

    @Test
    void should_DeleteAttributeTemplate_When_ValidId() {
        // Arrange
        when(attributeTemplateRepository.findById(eq(1L))).thenReturn(Optional.of(new AttributeTemplate("origin")));

        // Act
        attributeTemplateService.deleteAttributeTemplate(1L);

        // Verify
        verify(attributeTemplateRepository).findById(eq(1L));
        verify(attributeTemplateRepository).delete(any(AttributeTemplate.class));
    }

    @Test
    void should_ThrowResourceNotFoundException_When_DeletingInvalidId() {
        // Arrange
        when(attributeTemplateRepository.findById(eq(99L))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> attributeTemplateService.deleteAttributeTemplate(99L));

        verify(attributeTemplateRepository).findById(eq(99L));
        verifyNoMoreInteractions(attributeTemplateRepository);
    }

    @Test
    void should_UpdateAttributeTemplate_When_ValidNameProvided() {
        // Arrange
        AttributeTemplate existingTemplate = new AttributeTemplate("flavor");
        when(attributeTemplateRepository.findById(eq(1L))).thenReturn(Optional.of(existingTemplate));
        when(attributeTemplateRepository.existsByName("origin")).thenReturn(false);
        when(attributeTemplateRepository.save(any(AttributeTemplate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Capturing argument
        ArgumentCaptor<AttributeTemplate> captor = ArgumentCaptor.forClass(AttributeTemplate.class);

        // Act
        AttributeTemplate result = attributeTemplateService.updateAttributeTemplate(1L, dto);

        // Assert
        assertNotNull(result);
        assertEquals("origin", result.getName());

        // Verify
        verify(attributeTemplateRepository).findById(eq(1L));
        verify(attributeTemplateRepository).existsByName(eq("origin"));
        verify(attributeTemplateRepository).save(captor.capture());

        // Ensure name normalization
        AttributeTemplate captured = captor.getValue();
        assertEquals("origin", captured.getName());
    }

    @Test
    void should_ThrowResourceNotFoundException_When_UpdatingNonExistentId() {
        // Arrange
        when(attributeTemplateRepository.findById(eq(99L))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> attributeTemplateService.updateAttributeTemplate(99L, dto));

        verify(attributeTemplateRepository).findById(eq(99L));
        verifyNoMoreInteractions(attributeTemplateRepository);
    }

    @Test
    void should_FindById_When_ValidIdExists() {
        // Arrange
        when(attributeTemplateRepository.findById(eq(1L)))
                .thenReturn(Optional.of(new AttributeTemplate("Origin")));

        // Act
        AttributeTemplate result = attributeTemplateService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Origin", result.getName());

        verify(attributeTemplateRepository).findById(eq(1L));
    }

    @Test
    void should_ThrowResourceNotFoundException_When_FindingByNonExistentId() {
        // Arrange
        when(attributeTemplateRepository.findById(eq(99L))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> attributeTemplateService.findById(99L));

        verify(attributeTemplateRepository).findById(eq(99L));
        verifyNoMoreInteractions(attributeTemplateRepository);
    }

    @Test
    void should_FindAll_When_AttributeTemplatesExist() {
        // Arrange
        List<AttributeTemplate> templates = Arrays.asList(
                new AttributeTemplate("origin"),
                new AttributeTemplate("size"));
        when(attributeTemplateRepository.findAll()).thenReturn(templates);

        // Act
        List<AttributeTemplate> result = attributeTemplateService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(attributeTemplateRepository).findAll();
    }
}
