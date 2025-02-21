package com.makibeans.service;

import com.makibeans.dto.AttributeValueRequestDTO;
import com.makibeans.dto.AttributeValueResponseDTO;
import com.makibeans.dto.AttributeValueUpdateDTO;
import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.mapper.AttributeValueMapper;
import com.makibeans.model.AttributeTemplate;
import com.makibeans.model.AttributeValue;
import com.makibeans.repository.AttributeValueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttributeValueServiceTest {

    private AttributeTemplate attributeTemplate;

    @Mock
    private AttributeValueRepository attributeValueRepository;

    @Mock
    private AttributeTemplateService attributeTemplateService;

    @Mock
    private AttributeValueMapper mapper;

    @InjectMocks
    private AttributeValueService attributeValueService;

    /** GET **/
    @Test
    void whenGetAttributeValueById_thenReturnResponseDTO() {
        // Arrange
        AttributeValue attributeValue = new AttributeValue(attributeTemplate, "chili");
        AttributeValueResponseDTO responseDTO = new AttributeValueResponseDTO();
        responseDTO.setTemplateId(1L);
        responseDTO.setAttributeName("origin");
        responseDTO.setValue("chili");

        when(attributeValueRepository.findById(1L)).thenReturn(Optional.of(attributeValue));
        when(mapper.toResponseDTO(attributeValue)).thenReturn(responseDTO);

        // Act
        AttributeValueResponseDTO response = attributeValueService.getAttributeValueById(1L);

        // Assert
        assertNotNull(response);
        assertEquals("origin", response.getAttributeName());

        // Verify
        verify(attributeValueRepository).findById(1L);
        verify(mapper).toResponseDTO(attributeValue);
    }

    @Test
    void whenGetAttributeValueByIdWithNonExistentId_thenThrowResourceNotFoundException() {
        // Arrange
        when(attributeValueRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> attributeValueService.getAttributeValueById(1L));

        // Verify
        verify(attributeValueRepository).findById(1L);
    }

    @Test
    void whenGetAllAttributeValues_thenReturnListOfDTOs() {
        // Arrange
        AttributeValue attributeValue1 = new AttributeValue(attributeTemplate, "chili");
        AttributeValue attributeValue2 = new AttributeValue(attributeTemplate, "argentina");

        List<AttributeValue> attributeValues = List.of(attributeValue1, attributeValue2);
        when(attributeValueRepository.findAll()).thenReturn(attributeValues);
        when(mapper.toResponseDTO(any())).thenAnswer(invocation -> {
            AttributeValue entity = invocation.getArgument(0);
            AttributeValueResponseDTO dto = new AttributeValueResponseDTO();
            dto.setTemplateId(1L);
            dto.setAttributeName("origin");
            dto.setValue(entity.getValue());
            return dto;
        });

        // Act
        List<AttributeValueResponseDTO> response = attributeValueService.getAllAttributeValues();

        // Assert
        assertEquals(2, response.size());
        assertEquals("chili", response.get(0).getValue());
        assertEquals("argentina", response.get(1).getValue());

        // Verify
        verify(attributeValueRepository).findAll();
        verify(mapper, times(2)).toResponseDTO(any());
    }

    /** CREATE */
    @Test
    void whenCreateAttributeValue_thenReturnResponseDTO() {
        // Arrange
        AttributeValueRequestDTO requestDTO = new AttributeValueRequestDTO();
        requestDTO.setTemplateId(1L);
        requestDTO.setValue("chili");

        AttributeValue mappedEntity = new AttributeValue(attributeTemplate, "chili");
        AttributeValueResponseDTO responseDTO = new AttributeValueResponseDTO();
        responseDTO.setTemplateId(1L);
        responseDTO.setAttributeName("origin");
        responseDTO.setValue("chili");

        when(attributeTemplateService.findById(1L)).thenReturn(attributeTemplate);
        when(mapper.toEntity(requestDTO)).thenReturn(mappedEntity);
        when(attributeValueRepository.existsByValue(attributeTemplate, "chili")).thenReturn(false);
        when(mapper.toResponseDTO(any())).thenReturn(responseDTO);

        ArgumentCaptor<AttributeValue> captor = ArgumentCaptor.forClass(AttributeValue.class);

        // Act
        AttributeValueResponseDTO response = attributeValueService.createAttributeValue(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("origin", response.getAttributeName());

        // Verify
        verify(attributeValueRepository).save(captor.capture());
        assertEquals("chili", captor.getValue().getValue());
        verify(mapper).toResponseDTO(any());
    }

    @Test
    void whenCreateDAttributeValueWithDuplicateValue_thenThrowDuplicateResourceException() {
        // Arrange
        AttributeValueRequestDTO requestDTO = new AttributeValueRequestDTO();
        requestDTO.setTemplateId(1L);
        requestDTO.setValue("chili");

        attributeTemplate = new AttributeTemplate(); // Ensure attributeTemplate is initialized
        AttributeValue mappedEntity = new AttributeValue(attributeTemplate, "chili");

        when(attributeTemplateService.findById(1L)).thenReturn(attributeTemplate);
        when(mapper.toEntity(requestDTO)).thenReturn(mappedEntity);
        when(attributeValueRepository.existsByValue(attributeTemplate, "chili")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> attributeValueService.createAttributeValue(requestDTO));

        // Verify
        verify(attributeValueRepository, never()).save(any());
    }

    /** UPDATE */
    @Test
    void whenUpdateAttributeValue_thenReturnUpdatedDTO() {
        // Arrange
        AttributeValueUpdateDTO updateDTO = new AttributeValueUpdateDTO();
        updateDTO.setValue("Argentina");

        AttributeValue existingAttributeValue = new AttributeValue(attributeTemplate, "chili");
        when(attributeValueRepository.findById(1L)).thenReturn(Optional.of(existingAttributeValue));
        when(mapper.normalizeValue("Argentina")).thenReturn("argentina");
        when(attributeValueRepository.existsByValue(attributeTemplate, "argentina")).thenReturn(false);

        ArgumentCaptor<AttributeValue> captor = ArgumentCaptor.forClass(AttributeValue.class);

        // Act
        attributeValueService.updateAttributeValue(1L, updateDTO);

        // Verify
        verify(attributeValueRepository).save(captor.capture());
        assertEquals("argentina", captor.getValue().getValue());
    }

    @Test
    void whenUpdateAttributeValueWithDuplicateValue_thenThrowDuplicateResourceException() {
        // Arrange
        AttributeValueUpdateDTO updateDTO = new AttributeValueUpdateDTO();
        updateDTO.setValue("Argentina");

        AttributeValue existingAttributeValue = new AttributeValue(attributeTemplate, "chili");

        when(attributeValueRepository.findById(1L)).thenReturn(Optional.of(existingAttributeValue));
        when(mapper.normalizeValue("Argentina")).thenReturn("argentina");
        when(attributeValueRepository.existsByValue(attributeTemplate, "argentina")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> attributeValueService.updateAttributeValue(1L, updateDTO));

        // Verify
        verify(attributeValueRepository).findById(1L);
    }

    @Test
    void whenUpdateAttributeValueWithInvalidId_thenThrowResourceNotFoundException() {
        // Arrange
        AttributeValueUpdateDTO updateDTO = new AttributeValueUpdateDTO();
        updateDTO.setValue("Argentina");

        when(attributeValueRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> attributeValueService.updateAttributeValue(1L, updateDTO));

        // Verify
        verify(attributeValueRepository).findById(1L);
        verify(attributeValueRepository, never()).save(any());
    }

    /**  DELETE **/
    @Test
    void whenDeleteAttributeValue_thenSuccess() {
        // Arrange
        AttributeValue attributeValue = new AttributeValue(attributeTemplate, "chili");
        when(attributeValueRepository.findById(1L)).thenReturn(Optional.of(attributeValue));

        // Act
        attributeValueService.deleteAttributeValue(1L);

        // Verify
        verify(attributeValueRepository).findById(1L);
        verify(attributeValueRepository).delete(attributeValue);
    }

    @Test
    void whenDeleteAttributeValueWithNonExistentId_thenThrowResourceNotFoundException() {
        // Arrange
        when(attributeValueRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> attributeValueService.deleteAttributeValue(1L));

        // Verify
        verify(attributeValueRepository).findById(1L);
    }
}
