package com.makibeans.service;

import com.makibeans.dto.size.SizeRequestDTO;
import com.makibeans.dto.size.SizeResponseDTO;
import com.makibeans.dto.size.SizeUpdateDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.mapper.SizeMapper;
import com.makibeans.model.Size;
import com.makibeans.repository.SizeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SizeServiceTest {

    @Mock SizeRepository sizeRepository;
    @Mock SizeMapper sizeMapper;
    @Mock ProductVariantService productVariantService;
    @InjectMocks SizeService sizeService;

    Size size;

    @BeforeEach
    void setUp() {
        size = new Size("Medium");
    }

    @AfterEach
    void tearDown() {
        size = null;
    }

    // ========================================
    // CREATE
    // ========================================

    @Test
    void should_CreateSize_When_ValidInput() {
        // Arrange
        SizeRequestDTO sizeRequestDTO = new SizeRequestDTO("Medium");
        SizeResponseDTO expectedSizeResponseDTO = new SizeResponseDTO(1L, "Medium");

        when(sizeRepository.existsByName("medium")).thenReturn(false);
        when(sizeRepository.save(any())).thenReturn(size);
        when(sizeMapper.toResponseDTO(size)).thenReturn((expectedSizeResponseDTO));

        // Act
        SizeResponseDTO actualSizeResponseDTO = sizeService.createSize(sizeRequestDTO);

        // Assert
        assertNotNull(actualSizeResponseDTO, "SizeResponseDTO should not be null");
        assertEquals(actualSizeResponseDTO, expectedSizeResponseDTO, "SizeResponseDTO should match expected");

        // Verify
        verify(sizeRepository).existsByName("medium");
        verify(sizeRepository).save(any());
        verifyNoMoreInteractions(sizeRepository);
    }

    @Test
    void should_ThrowDuplicateException_When_SizeNameExists() {
        // Arrange
        SizeRequestDTO sizeRequestDTO = new SizeRequestDTO("Large");
        when(sizeRepository.existsByName("large")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> sizeService.createSize(sizeRequestDTO));

        // Verify
        verify(sizeRepository).existsByName("large");
        verifyNoMoreInteractions(sizeRepository);
    }

    // ========================================
    // DELETE
    // ========================================

    @Test
    void should_DeleteSize_When_Exists() {
        // Arrange
        when(sizeRepository.findById(1L)).thenReturn(Optional.of(size));

        // Act
        sizeService.deleteSize(1L);

        // Verify
        verify(productVariantService).deleteProductVariantBySizeId(1L);
        verify(sizeRepository).findById(1L);
        verify(sizeRepository).delete(any(Size.class));
        verifyNoMoreInteractions(sizeRepository);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_DeletingNonExistentSize() {
        // Arrange
        when(sizeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> sizeService.deleteSize(99L),
                "Expected ResourceNotFoundException when deleting non-existent size");

        // Verify
        verify(sizeRepository).findById(99L);
        verifyNoMoreInteractions(sizeRepository);
    }

    // ========================================
    // UPDATE
    // ========================================

    @Test
    void should_UpdateSize_When_ValidInput() {
        // Arrange
        SizeUpdateDTO sizeUpdateDTO = new SizeUpdateDTO("XL");
        SizeResponseDTO expectedSizeResponseDTO = new SizeResponseDTO(1L, "XL");

        when(sizeRepository.findById(1L)).thenReturn(Optional.of(size));
        when(sizeRepository.existsByName("xl")).thenReturn(false);
        when(sizeRepository.save(any(Size.class))).thenReturn(size);
        when(sizeMapper.toResponseDTO(size)).thenReturn(expectedSizeResponseDTO);

        // Act
        SizeResponseDTO actualSizeResponseDTO = sizeService.updateSize(1L, sizeUpdateDTO);

        // Assert
        assertNotNull(actualSizeResponseDTO, "SizeResponseDTO should not be null");
        assertEquals(expectedSizeResponseDTO, actualSizeResponseDTO, "SizeResponseDTO should match expected");

        // Verify
        verify(sizeRepository).findById(1L);
        verify(sizeRepository).existsByName("xl");
        verify(sizeRepository).save(any(Size.class));
        verifyNoMoreInteractions(sizeRepository);
    }

    @Test
    void should_ThrowDuplicateException_When_UpdatingToExistingName() {
        // Arrange
        SizeUpdateDTO dto = new SizeUpdateDTO("XL");
        when(sizeRepository.findById(1L)).thenReturn(Optional.of(size));
        when(sizeRepository.existsByName("xl")).thenReturn(true);

        // Act & Assert
        assertThrows(
                DuplicateResourceException.class,
                () -> sizeService.updateSize(1L, dto),
                "Expected DuplicateResourceException when updating to an existing size name");

        // Verify
        verify(sizeRepository).findById(1L);
        verify(sizeRepository).existsByName("xl");
        verifyNoMoreInteractions(sizeRepository);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_UpdatingNonExistentSize() {
        // Arrange
        SizeUpdateDTO dto = new SizeUpdateDTO("XL");
        when(sizeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> sizeService.updateSize(99L, dto),
                "Expected ResourceNotFoundException when updating non-existent size");


        // Verify
        verify(sizeRepository).findById(99L);
        verifyNoMoreInteractions(sizeRepository);
    }

    // ========================================
    // GET BY ID
    // ========================================

    @Test
    void should_ReturnSize_When_IdExists() {
        // Arrange
        SizeResponseDTO expectedSizeResponseDTO = new SizeResponseDTO(1L, "Medium");

        when(sizeRepository.findById(1L)).thenReturn(Optional.of(size));
        when(sizeMapper.toResponseDTO(size)).thenReturn(expectedSizeResponseDTO);

        // Act
        SizeResponseDTO actualSizeResponseDTO = sizeService.getSizeById(1L);

        // Assert
        assertEquals(expectedSizeResponseDTO, actualSizeResponseDTO);

        // Verify
        verify(sizeRepository).findById(1L);
        verify(sizeMapper).toResponseDTO(size);
        verifyNoMoreInteractions(sizeRepository);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_IdDoesNotExist() {
        // Arrange
        when(sizeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> sizeService.getSizeById(99L),
                "Expected ResourceNotFoundException when size ID does not exist");

        // Verify
        verify(sizeRepository).findById(99L);
        verifyNoMoreInteractions(sizeRepository);
    }

    // ========================================
    // GET ALL
    // ========================================

    @Test
    void should_ReturnAllSizes() {
        // Arrange
        Size size1 = new Size("Medium");
        Size size2 = new Size("Large");
        SizeResponseDTO sizeResponseDTO1 = new SizeResponseDTO(1L, "Medium");
        SizeResponseDTO sizeResponseDTO2 = new SizeResponseDTO(2L, "Large");

        when(sizeRepository.findAll()).thenReturn(List.of(size1,size2));
        when(sizeMapper.toResponseDTO(size1)).thenReturn((sizeResponseDTO1));
        when(sizeMapper.toResponseDTO(size2)).thenReturn((sizeResponseDTO2));

        // Act
        List<SizeResponseDTO> result = sizeService.getAllSizes();

        // Assert
        assertEquals(2, result.size());
        assertNotNull(result, "Result should not be null");
        assertEquals(sizeResponseDTO1, result.get(0), "First size should match");
        assertEquals(sizeResponseDTO2, result.get(1), "Second size should match");

        // Verify
        verify(sizeRepository).findAll();
        verify(sizeMapper).toResponseDTO(size1);
        verify(sizeMapper).toResponseDTO(size2);
        verifyNoMoreInteractions(sizeRepository);
    }

    // ========================================
    // FILTER
    // ========================================

    @Test
    void should_FilterSizes_ByName() {
        // Arrange
        Size size1 = new Size("Medium");
        Size size2 = new Size("Large");
        Size size3 = new Size("Small");
        SizeResponseDTO sizeResponseDTO1 = new SizeResponseDTO(1L, "Medium");
        Map<String, String> params = Map.of("name", "medium");

        when(sizeRepository.findAll()).thenReturn(List.of(size1, size2, size3));
        when(sizeMapper.toResponseDTO(size1)).thenReturn(sizeResponseDTO1);

        // Act
        List<SizeResponseDTO> result = sizeService.findBySearchQuery(params);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size(), "Only one size should match the filter");
        assertEquals(sizeResponseDTO1, result.get(0), "Filtered size should match");

        // Verify
        verify(sizeRepository).findAll();
        verify(sizeMapper).toResponseDTO(size1);
        verifyNoMoreInteractions(sizeRepository);
    }
}