package com.makibeans.service;

import com.makibeans.dto.SizeCreateDTO;
import com.makibeans.dto.SizeUpdateDTO;
import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.model.Size;
import com.makibeans.repository.SizeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SizeServiceTest {

    private Size size;
    private SizeCreateDTO sizeCreateDTO;
    private SizeUpdateDTO sizeUpdateDTO;

    @Mock
    SizeRepository sizeRepository;

    @InjectMocks
    SizeService sizeService;

    @BeforeEach
    void setUp() {
        size = new Size("Size");
        sizeCreateDTO = new SizeCreateDTO();
        sizeCreateDTO.setName("Size");

        sizeUpdateDTO = new SizeUpdateDTO();
        sizeUpdateDTO.setName("UpdatedSize");
    }

    @AfterEach
    void tearDown() {
        size = null;
        sizeCreateDTO = null;
        sizeUpdateDTO = null;
    }

    @Test
    void testCreateSizeSuccess() {
        // Arrange
        when(sizeRepository.existsByName(any(String.class))).thenReturn(false);
        when(sizeRepository.save(any(Size.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Size result = sizeService.createSize(sizeCreateDTO);

        // Assert
        assertNotNull(result, "The created Size should not be null.");
        assertEquals("size", result.getName(), "The created Size should have the expected name.");

        // Verify
        verify(sizeRepository).existsByName(eq("size")); // Normalized
        verify(sizeRepository).save(any(Size.class));
        verifyNoMoreInteractions(sizeRepository);
    }

    @Test
    void testCreateSizeDuplicateResourceException() {
        // Arrange
        SizeCreateDTO duplicateDTO = new SizeCreateDTO();
        duplicateDTO.setName("duplicatesize");

        when(sizeRepository.existsByName("duplicatesize")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class,
                () -> sizeService.createSize(duplicateDTO),
                "Expected DuplicateResourceException when trying to create a Size with a name that already exists.");

        // Verify interactions
        verify(sizeRepository).existsByName(eq("duplicatesize"));
        verifyNoMoreInteractions(sizeRepository);
    }

    @Test
    void testDeleteSizeSuccess() {
        // Arrange
        when(sizeRepository.findById(1L)).thenReturn(Optional.of(size));

        // Act
        sizeService.deleteSize(1L);

        // Verify interactions
        verify(sizeRepository).findById(eq(1L));
        verify(sizeRepository).delete(any(Size.class));
        verifyNoMoreInteractions(sizeRepository);
    }

    @Test
    void testDeleteSizeResourceNotFoundException() {
        // Arrange
        when(sizeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> sizeService.deleteSize(99L),
                "Expected ResourceNotFoundException when trying to delete a non-existent size.");

        // Verify interactions
        verify(sizeRepository).findById(eq(99L));
        verify(sizeRepository, never()).delete(any(Size.class));
        verifyNoMoreInteractions(sizeRepository);
    }

    @Test
    void testUpdateSizeSuccess() {
        // Arrange
        when(sizeRepository.findById(1L)).thenReturn(Optional.of(size));
        when(sizeRepository.existsByName("updatedsize")).thenReturn(false);
        when(sizeRepository.save(any(Size.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Declare captor for Size
        ArgumentCaptor<Size> sizeCaptor = ArgumentCaptor.forClass(Size.class);

        // Act
        Size updatedSize = sizeService.updateSize(1L, sizeUpdateDTO);

        // Assert
        assertNotNull(updatedSize, "The updated Size should not be null.");
        assertEquals("updatedsize", updatedSize.getName(), "The updated Size should have the new name.");

        // Verify interactions
        verify(sizeRepository).findById(eq(1L));
        verify(sizeRepository).existsByName(eq("updatedsize"));
        verify(sizeRepository).save(sizeCaptor.capture()); // Capture the argument passed to save

        // Retrieve the captured Size object
        Size capturedSize = sizeCaptor.getValue();
        assertEquals("updatedsize", capturedSize.getName(), "The Size object saved should have the updated name.");

        verifyNoMoreInteractions(sizeRepository);
    }

    @Test
    void testUpdateSizeDuplicateResourceException(){
        // Arrange
        SizeUpdateDTO duplicateDTO = new SizeUpdateDTO();
        duplicateDTO.setName("DuplicateSize");

        when(sizeRepository.findById(1L)).thenReturn(Optional.of(size));
        when(sizeRepository.existsByName("duplicatesize")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class,
                () -> sizeService.updateSize(1L, duplicateDTO),
                "Expected DuplicateResourceException when trying to update a size to an existing name.");

        // Verify
        verify(sizeRepository).findById(eq(1L));
        verify(sizeRepository).existsByName(eq("duplicatesize"));
        verifyNoMoreInteractions(sizeRepository);
    }

    @Test
    void testUpdateSizeResourceNotFoundException() {
        // Arrange
        when(sizeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> sizeService.updateSize(99L, sizeUpdateDTO),
                "Expected ResourceNotFoundException when trying to update a non-existent size.");

        // Verify
        verify(sizeRepository).findById(eq(99L));
        verifyNoMoreInteractions(sizeRepository);
    }
}
