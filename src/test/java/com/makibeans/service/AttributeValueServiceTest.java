package com.makibeans.service;

import com.makibeans.dto.AttributeValueRequestDTO;
import com.makibeans.dto.AttributeValueResponseDTO;
import com.makibeans.dto.AttributeValueUpdateDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.mapper.AttributeValueMapper;
import com.makibeans.model.AttributeTemplate;
import com.makibeans.model.AttributeValue;
import com.makibeans.repository.AttributeValueRepository;
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
 * Unit tests for AttributeValueService
 */

@ExtendWith(MockitoExtension.class)
class AttributeValueServiceTest {

    @Mock
    private AttributeValueRepository attributeValueRepository;

    @Mock
    private AttributeTemplateService attributeTemplateService;

    @Mock
    private ProductAttributeService productAttributeService;

    @Mock
    private AttributeValueMapper mapper;

    @InjectMocks
    private AttributeValueService attributeValueService;

    private AttributeTemplate template;
    private AttributeValue attributeValue;
    private AttributeValueRequestDTO requestDTO;
    private AttributeValueUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        // Arrange
        template = new AttributeTemplate("Origin");
        attributeValue = new AttributeValue(template, "ethiopia");

        requestDTO = new AttributeValueRequestDTO();
        requestDTO.setTemplateId(1L);
        requestDTO.setValue("  Ethiopia ");

        updateDTO = new AttributeValueUpdateDTO();
        updateDTO.setValue("Colombia");
    }

    // ========================================
    // CREATE
    // ========================================

    @Test
    void should_CreateAttributeValue_When_ValidInput() {
        // Arrange
        when(attributeTemplateService.findById(1L)).thenReturn(template);
        when(attributeValueRepository.existsByValue(template, "ethiopia")).thenReturn(false);
        when(attributeValueRepository.save(any())).thenReturn(attributeValue);
        when(mapper.toResponseDTO(attributeValue))
                .thenReturn(new AttributeValueResponseDTO(10L, 1L, "Origin", "ethiopia"));

        // Act
        AttributeValueResponseDTO result = attributeValueService.createAttributeValue(requestDTO);

        // Assert
        assertEquals(10L, result.getId(), "Expected ID to be 10");
        assertEquals(1L, result.getAttributeTemplateId(), "Expected AttributeTemplateId to be 1");
        assertEquals("Origin", result.getAttributeTemplateName(), "Expected template name to be 'Origin'");
        assertEquals("ethiopia", result.getValue(), "Expected value to be 'ethiopia'");

        // Verify
        verify(attributeTemplateService).findById(1L);
        verify(attributeValueRepository).existsByValue(template, "ethiopia");
        verify(attributeValueRepository).save(any());
        verify(mapper).toResponseDTO(attributeValue);
        verifyNoMoreInteractions(attributeValueRepository, attributeTemplateService, mapper, productAttributeService);
    }

    @Test
    void should_ThrowDuplicateResourceException_When_AttributeValueAlreadyExists() {
        // Arrange
        when(attributeTemplateService.findById(1L)).thenReturn(template);
        when(attributeValueRepository.existsByValue(template, "ethiopia")).thenReturn(true);

        // Act & Assert
        assertThrows(
                DuplicateResourceException.class,
                () -> attributeValueService.createAttributeValue(requestDTO),
                "Expected DuplicateResourceException when value already exists");

        // Verify
        verify(attributeTemplateService).findById(1L);
        verify(attributeValueRepository).existsByValue(template, "ethiopia");
        verifyNoMoreInteractions(attributeValueRepository, attributeTemplateService, mapper, productAttributeService);
    }

    // ========================================
    // DELETE
    // ========================================

    @Test
    void should_DeleteAttributeValueAndRemoveFromProductAttributes_When_ValidId() {
        // Arrange
        AttributeValue attributeValue = mock(AttributeValue.class);
        ReflectionTestUtils.setField(attributeValue, "id", 1L);
        doNothing().when(productAttributeService).deleteAttributeValuesByAttributeValueId(1L);
        when(attributeValueRepository.findById(1L)).thenReturn(Optional.of(attributeValue));
        doNothing().when(attributeValueRepository).delete(attributeValue);

        // Act
        attributeValueService.deleteAttributeValue(1L);

        // Verify
        verify(attributeValueRepository, times(2)).findById(1L);
        verify(productAttributeService).deleteAttributeValuesByAttributeValueId(1L);
        verify(attributeValueRepository).delete(attributeValue);
        verifyNoMoreInteractions(attributeValueRepository, attributeTemplateService, mapper, productAttributeService);
    }

    // ========================================
    // UPDATE
    // ========================================

    @Test
    void should_UpdateAttributeValue_When_ValueChanged() {
        // Arrange
        when(attributeValueRepository.findById(1L)).thenReturn(Optional.of(attributeValue));
        when(attributeValueRepository.existsByValue(template, "colombia")).thenReturn(false);
        when(attributeValueRepository.save(attributeValue)).thenReturn(attributeValue);
        when(mapper.toResponseDTO(attributeValue)).thenReturn(
                new AttributeValueResponseDTO(1L, 1L, "Origin", "colombia"));

        // Act
        AttributeValueResponseDTO result = attributeValueService.updateAttributeValue(1L, updateDTO);

        // Assert
        assertEquals("colombia", result.getValue(), "Expected updated value to be 'colombia'");

        // Verify
        verify(attributeValueRepository).findById(1L);
        verify(attributeValueRepository).existsByValue(template, "colombia");
        verify(attributeValueRepository).save(attributeValue);
        verify(mapper).toResponseDTO(attributeValue);
        verifyNoMoreInteractions(attributeValueRepository, attributeTemplateService, mapper, productAttributeService);
    }

    @Test
    void should_NotUpdateAttributeValue_When_SameValue() {
        // Arrange
        AttributeTemplate template = new AttributeTemplate("Origin");
        attributeValue = new AttributeValue(template, "ethiopia");
        updateDTO.setValue("  ethiopia  ");
        AttributeValueResponseDTO expectedResponseDTO = new AttributeValueResponseDTO(1L, null, "Origin", "ethiopia");

        when(attributeValueRepository.findById(1L)).thenReturn(Optional.of(attributeValue));
        when(mapper.toResponseDTO(attributeValue)).thenReturn(expectedResponseDTO);

        // Act
        AttributeValueResponseDTO result = attributeValueService.updateAttributeValue(1L, updateDTO);

        // Assert
        assertNotNull(result, "Expected result to be not null");
        assertEquals(expectedResponseDTO, result, "Expected result to match expected DTO");

        // Verify
        verify(attributeValueRepository).findById(1L);
        verify(mapper).toResponseDTO(attributeValue);
        verifyNoMoreInteractions(attributeValueRepository, attributeTemplateService, mapper, productAttributeService);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_UpdateWithInvalidId() {
        // Arrange
        when(attributeValueRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> attributeValueService.updateAttributeValue(99L, updateDTO),
                "Expected ResourceNotFoundException for invalid update ID");

        // Verify
        verify(attributeValueRepository).findById(99L);
        verifyNoMoreInteractions(attributeValueRepository, attributeTemplateService, mapper, productAttributeService);
    }

    @Test
    void should_ThrowDuplicateResourceException_When_UpdatingWithExistingValue() {
        // Arrange
        when(attributeValueRepository.findById(1L)).thenReturn(Optional.of(attributeValue));
        when(attributeValueRepository.existsByValue(template, "colombia")).thenReturn(true);

        updateDTO.setValue("Colombia");

        // Act & Assert
        assertThrows(DuplicateResourceException.class,
                () -> attributeValueService.updateAttributeValue(1L, updateDTO),
                "Expected DuplicateResourceException when updating with duplicate value");

        // Verify
        verify(attributeValueRepository).findById(1L);
        verify(attributeValueRepository).existsByValue(template, "colombia");
        verifyNoMoreInteractions(attributeValueRepository, attributeTemplateService, mapper, productAttributeService);
    }

    // ========================================
    // GET BY ID
    // ========================================

    @Test
    void should_ReturnDTO_When_IdExists() {
        // Arrange
        AttributeValueResponseDTO expectedResponseDTO = new AttributeValueResponseDTO(10L, 1L, "Origin", "ethiopia");;
        when(attributeValueRepository.findById(10L)).thenReturn(Optional.of(attributeValue));
        when(mapper.toResponseDTO(attributeValue)).thenReturn(expectedResponseDTO);

        // Act
        AttributeValueResponseDTO result = attributeValueService.getAttributeValueById(10L);

        // Assert
        assertNotNull(result, "Expected result to be not null");
        assertEquals(expectedResponseDTO, result, "Expected result to match expected DTO");

        // Verify
        verify(attributeValueRepository).findById(10L);
        verify(mapper).toResponseDTO(attributeValue);
        verifyNoMoreInteractions(attributeValueRepository, attributeTemplateService, mapper, productAttributeService);
    }

    @Test
    void should_ReturnAllAttributeValues_ByTemplateId() {
        // Arrange
        AttributeTemplate template = new AttributeTemplate("Origin");
        AttributeValue value = new AttributeValue(template, "Brazil");
        AttributeValueResponseDTO expectedResponseDTO = new AttributeValueResponseDTO(1L, 1L, "Origin", "Brazil");

        when(attributeTemplateService.findById(1L)).thenReturn(template);
        when(attributeValueRepository.findAllByAttributeTemplate(template)).thenReturn(List.of(value));
        when(mapper.toResponseDTO(value)).thenReturn(expectedResponseDTO);

        // Act
        List<AttributeValueResponseDTO> result = attributeValueService.getAllAttributeValuesByTemplateId(1L);

        // Assert
        assertNotNull(result, "Expected result to be not null");
        assertEquals(1, result.size(), "Expected result size to be 1");
        assertEquals(expectedResponseDTO, result.get(0), "Expected result to match expected DTO");

        // Verify
        verify(attributeTemplateService).findById(1L);
        verify(attributeValueRepository).findAllByAttributeTemplate(template);
        verify(mapper).toResponseDTO(value);
        verifyNoMoreInteractions(attributeTemplateService, attributeValueRepository, mapper, productAttributeService);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_GetByInvalidId() {
        // Arrange
        when(attributeValueRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> attributeValueService.getAttributeValueById(99L),
                "Expected ResourceNotFoundException for invalid ID");

        // Verify
        verify(attributeValueRepository).findById(99L);
        verifyNoMoreInteractions(attributeValueRepository, attributeTemplateService, mapper, productAttributeService);
    }

    // ========================================
    // FILTER
    // ========================================

    @Test
    void should_FilterAttributeValues_ByValue() {
        // Arrange
        AttributeTemplate template = new AttributeTemplate();
        template.setName("Color");

        AttributeValue value = new AttributeValue(template, "Red");
        AttributeValueResponseDTO expectedDTO = new AttributeValueResponseDTO(1L, 1L, "Color", "Red");

        Map<String, String> params = Map.of("value", "red");

        when(attributeValueRepository.findAll()).thenReturn(List.of(value));
        when(mapper.toResponseDTO(value)).thenReturn(expectedDTO);

        // Act
        List<AttributeValueResponseDTO> result = attributeValueService.findBySearchQuery(params);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Expected 1 result from filtered list");
        assertEquals(expectedDTO, result.get(0), "Expected returned DTO to match expected DTO");

        // Verify
        verify(attributeValueRepository).findAll();
        verify(mapper).toResponseDTO(value);
        verifyNoMoreInteractions(attributeValueRepository, mapper);
    }
}