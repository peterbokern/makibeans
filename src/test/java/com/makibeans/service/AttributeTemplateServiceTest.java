package com.makibeans.service;

import com.makibeans.dto.attributetemplate.AttributeTemplateRequestDTO;
import com.makibeans.dto.attributetemplate.AttributeTemplateResponseDTO;
import com.makibeans.dto.attributetemplate.AttributeTemplateUpdateDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.mapper.AttributeTemplateMapper;
import com.makibeans.model.AttributeTemplate;
import com.makibeans.model.ProductAttribute;
import com.makibeans.repository.AttributeTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AttributeTemplateService class.
 */

@ExtendWith(MockitoExtension.class)
class AttributeTemplateServiceTest {

    @Mock
    private AttributeTemplateRepository attributeTemplateRepository;

    @Mock
    private AttributeTemplateMapper mapper;

    @Mock
    private ProductAttributeService productAttributeService;

    @InjectMocks
    private AttributeTemplateService attributeTemplateService;

    private AttributeTemplateRequestDTO requestDTO;
    private AttributeTemplateUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        // Arrange
        requestDTO = new AttributeTemplateRequestDTO();
        requestDTO.setName("  Origin  ");

        updateDTO = new AttributeTemplateUpdateDTO();
        updateDTO.setName("  Origin  ");
    }

    // ========================================
    // CREATE
    // ========================================

    @Test
    void should_CreateAttributeTemplate_When_ValidNameProvided() {
        // Arrange
        when(attributeTemplateRepository.existsByName("origin")).thenReturn(false);
        when(attributeTemplateRepository.save(any(AttributeTemplate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponseDTO(any())).thenAnswer(invocation -> {
            AttributeTemplate at = invocation.getArgument(0);
            return new AttributeTemplateResponseDTO(at.getId(), at.getName());
        });

        // Act
        AttributeTemplateResponseDTO result = attributeTemplateService.createAttributeTemplate(requestDTO);

        // Assert
        assertNotNull(result, "Expected result to be not null");
        assertEquals("origin", result.getName(), "Expected result name to be 'origin'");

        // Verify
        verify(attributeTemplateRepository).existsByName("origin");
        verify(attributeTemplateRepository).save(any(AttributeTemplate.class));
        verify(mapper).toResponseDTO(any());
        verifyNoMoreInteractions(attributeTemplateRepository, mapper, productAttributeService);
    }

    @Test
    void should_ThrowDuplicateResourceException_When_NameAlreadyExists() {
        // Arrange
        when(attributeTemplateRepository.existsByName("origin")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class,
                () -> attributeTemplateService.createAttributeTemplate(requestDTO),
                "Expected exception: DuplicateResourceException");

        // Verify
        verify(attributeTemplateRepository).existsByName("origin");
        verifyNoMoreInteractions(attributeTemplateRepository, mapper, productAttributeService);
    }

    // ========================================
    // DELETE
    // ========================================

    @Test
    void should_DeleteAttributeTemplateAndAssociatedProductAttributes_When_IdExists() {
        // Arrange
        AttributeTemplate template = new AttributeTemplate("origin");
        ReflectionTestUtils.setField(template, "id", 1L);
        ProductAttribute productAttribute = mock(ProductAttribute.class);

        when(attributeTemplateRepository.findById(1L)).thenReturn(Optional.of(template));
        when(productAttributeService.getProductAttributesByTemplateId(1L)).thenReturn(List.of(productAttribute));
        when(productAttribute.getId()).thenReturn(99L);

        // Act
        attributeTemplateService.deleteAttributeTemplate(1L);

        // Verify
        verify(productAttributeService).getProductAttributesByTemplateId(1L);
        verify(productAttributeService).deleteProductAttribute(99L);
        verify(attributeTemplateRepository, times(2)).findById(1L);
        verify(attributeTemplateRepository).delete(template);
        verifyNoMoreInteractions(attributeTemplateRepository, productAttributeService, mapper);
    }

    @Test
    void should_DeleteOnlyAttributeTemplate_When_NoAssociatedProductAttributesExist() {
        // Arrange
        AttributeTemplate template = new AttributeTemplate("origin");
        ReflectionTestUtils.setField(template, "id", 1L);

        when(attributeTemplateRepository.findById(1L)).thenReturn(Optional.of(template));
        when(productAttributeService.getProductAttributesByTemplateId(1L)).thenReturn(Collections.emptyList());

        // Act
        attributeTemplateService.deleteAttributeTemplate(1L);

        // Verify
        verify(productAttributeService).getProductAttributesByTemplateId(1L);
        verify(productAttributeService, never()).deleteProductAttribute(any());
        verify(attributeTemplateRepository, times(2)).findById(1L);
        verify(attributeTemplateRepository).delete(template);
        verifyNoMoreInteractions(attributeTemplateRepository, productAttributeService, mapper);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_DeleteWithInvalidId() {
        // Arrange
        when(attributeTemplateRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> attributeTemplateService.deleteAttributeTemplate(99L),
                "Expected exception: ResourceNotFoundException");

        // Verify
        verify(attributeTemplateRepository).findById(99L);
        verifyNoMoreInteractions(attributeTemplateRepository);
    }

    // ========================================
    // UPDATE
    // ========================================

    @Test
    void should_UpdateAttributeTemplate_When_NameChanges() {
        // Arrange
        AttributeTemplate template = new AttributeTemplate("flavor");

        when(attributeTemplateRepository.findById(1L)).thenReturn(Optional.of(template));
        when(attributeTemplateRepository.existsByName("origin")).thenReturn(false);
        when(attributeTemplateRepository.save(any(AttributeTemplate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponseDTO(any())).thenAnswer(invocation -> {
            AttributeTemplate at = invocation.getArgument(0);
            return new AttributeTemplateResponseDTO(at.getId(), at.getName());
        });

        // Act
        AttributeTemplateResponseDTO result = attributeTemplateService.updateAttributeTemplate(1L, updateDTO);

        // Assert
        assertNotNull(result, "Expected result to be not null");
        assertEquals("origin", result.getName(), "Expected result name to be 'origin'");

        // Verify
        verify(attributeTemplateRepository).findById(1L);
        verify(attributeTemplateRepository).existsByName("origin");
        verify(attributeTemplateRepository).save(any(AttributeTemplate.class));
        verify(mapper).toResponseDTO(any());
        verifyNoMoreInteractions(attributeTemplateRepository, mapper, productAttributeService);
    }

    @Test
    void should_NotUpdateAttributeTemplate_When_NameIsUnchanged() {
        // Arrange
        AttributeTemplate template = new AttributeTemplate("origin");

        when(attributeTemplateRepository.findById(1L)).thenReturn(Optional.of(template));
        when(mapper.toResponseDTO(template)).thenReturn(new AttributeTemplateResponseDTO(null, "origin"));

        // Act
        AttributeTemplateResponseDTO result = attributeTemplateService.updateAttributeTemplate(1L, updateDTO);

        // Assert
        assertNotNull(result, "Expected result to be not null");
        assertEquals("origin", result.getName(), "Expected name to remain unchanged");

        // Verify
        verify(attributeTemplateRepository).findById(1L);
        verify(mapper).toResponseDTO(template);
        verifyNoMoreInteractions(attributeTemplateRepository, mapper, productAttributeService);
    }

    @Test
    void should_ThrowDuplicateResourceException_When_UpdatingWithExistingName() {
        // Arrange
        AttributeTemplate template = new AttributeTemplate("flavor");

        when(attributeTemplateRepository.findById(1L)).thenReturn(Optional.of(template));
        when(attributeTemplateRepository.existsByName("origin")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class,
                () -> attributeTemplateService.updateAttributeTemplate(1L, updateDTO),
                "Expected exception: DuplicateResourceException");

        // Verify
        verify(attributeTemplateRepository).findById(1L);
        verify(attributeTemplateRepository).existsByName("origin");
        verifyNoMoreInteractions(attributeTemplateRepository, mapper, productAttributeService);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_UpdateWithInvalidId() {
        // Arrange
        when(attributeTemplateRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> attributeTemplateService.updateAttributeTemplate(99L, updateDTO),
                "Expected exception: ResourceNotFoundException");

        // Verify
        verify(attributeTemplateRepository).findById(99L);
        verifyNoMoreInteractions(attributeTemplateRepository, mapper, productAttributeService);
    }

    // ========================================
    // GET BY ID
    // ========================================

    @Test
    void should_ReturnDTO_When_IdExists() {
        // Arrange
        AttributeTemplate template = new AttributeTemplate("origin");

        when(attributeTemplateRepository.findById(1L)).thenReturn(Optional.of(template));
        when(mapper.toResponseDTO(template)).thenReturn(new AttributeTemplateResponseDTO(null, "origin"));

        // Act
        AttributeTemplateResponseDTO result = attributeTemplateService.getAttributeTemplateById(1L);

        // Assert
        assertNotNull(result, "Expected result to be not null");
        assertEquals("origin", result.getName(), "Expected result name to be 'origin'");

        // Verify
        verify(attributeTemplateRepository).findById(1L);
        verify(mapper).toResponseDTO(template);
        verifyNoMoreInteractions(attributeTemplateRepository, mapper, productAttributeService);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_GetByInvalidId() {
        // Arrange
        when(attributeTemplateRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> attributeTemplateService.getAttributeTemplateById(99L),
                "Expected exception: ResourceNotFoundException");

        // Verify
        verify(attributeTemplateRepository).findById(99L);
        verifyNoMoreInteractions(attributeTemplateRepository, mapper, productAttributeService);
    }

    // ========================================
    // VALID KEYS / CACHING
    // ========================================

    @Test
    void should_ReturnValidAttributeKeys_When_AttributeTemplatesExist() {
        // Arrange
        List<AttributeTemplate> templates = List.of(
                new AttributeTemplate("Origin"),
                new AttributeTemplate("Size"));

        when(attributeTemplateRepository.findAll()).thenReturn(templates);

        // Act
        Set<String> result = attributeTemplateService.getValidAttributeKeys();

        // Assert
        assertEquals(Set.of("origin", "size"), result, "Expected keys to be normalized and unique");

        // Verify
        verify(attributeTemplateRepository).findAll();
        verifyNoMoreInteractions(attributeTemplateRepository, mapper, productAttributeService);
    }

    @Test
    void should_RefreshCacheWithoutError() {
        // Act & Assert
        assertDoesNotThrow(() -> attributeTemplateService.refreshAttributeCache(),
                "Expected no exception to be thrown");

        // Verify
        verifyNoInteractions(attributeTemplateRepository, mapper, productAttributeService);
    }

    // ========================================
    // FILTER
    // ========================================

    @Test
    void should_FilterTemplates_ByName() {
        // Arrange
        Map<String, String> params = Map.of("name", "flavor");
        AttributeTemplate template = new AttributeTemplate("Flavor");
        AttributeTemplateResponseDTO expectedDTO = new AttributeTemplateResponseDTO(1L, "Flavor");

        when(attributeTemplateRepository.findAll()).thenReturn(List.of(template));
        when(mapper.toResponseDTO(template)).thenReturn(expectedDTO);

        // Act
        List<AttributeTemplateResponseDTO> result = attributeTemplateService.findBySearchQuery(params);

        // Assert
        assertNotNull(result, "Result list should not be null");
        assertEquals(1, result.size(), "Expected exactly one result matching 'flavor'");
        assertEquals(expectedDTO, result.get(0), "Returned DTO should match expected template DTO");

        // Verify
        verify(attributeTemplateRepository).findAll();
        verify(mapper).toResponseDTO(template);
        verifyNoMoreInteractions(attributeTemplateRepository, mapper);
    }
}
