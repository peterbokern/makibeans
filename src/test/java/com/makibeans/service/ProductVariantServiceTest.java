package com.makibeans.service;

import com.makibeans.dto.productvariant.ProductVariantRequestDTO;
import com.makibeans.dto.productvariant.ProductVariantResponseDTO;
import com.makibeans.dto.productvariant.ProductVariantUpdateDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.mapper.ProductVariantMapper;
import com.makibeans.model.Category;
import com.makibeans.model.Product;
import com.makibeans.model.ProductVariant;
import com.makibeans.model.Size;
import com.makibeans.repository.ProductVariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductVariantServiceTest {

    @Mock ProductVariantRepository productVariantRepository;
    @Mock ProductService productService;
    @Mock SizeService sizeService;
    @Mock ProductVariantMapper productVariantMapper;

    @InjectMocks ProductVariantService productVariantService;

    Product product;
    Size size;
    ProductVariant variant;

    @BeforeEach
    void setup() {
        size = new Size("Small");
        product = new Product("Espresso", "Smooth", null, new Category("Coffee", "Rich"));
        variant = new ProductVariant(product, size, 1000L, "ESP-SM-0001", 10L);
    }

    // ========================================
    // GET BY ID
    // ========================================

    @Test
    void should_ReturnVariant_When_IdExists() {
        // Arrange
        ProductVariant variant = new ProductVariant(product, size, 1000L, "ESP-SM-0001", 10L);
        ProductVariantResponseDTO expectedResponseDTO = new ProductVariantResponseDTO(null, product.getId(), size.getName(), "ESP-SM-0001", 1000L, 10L);

        when(productVariantRepository.findById(1L)).thenReturn(Optional.of(variant));
        when(productVariantMapper.toResponseDTO(variant)).thenReturn(expectedResponseDTO);

        // Act
        ProductVariantResponseDTO actualResponseDTO = productVariantService.getProductVariantById(1L);

        // Assert
        assertNotNull(actualResponseDTO);
        assertEquals(expectedResponseDTO, actualResponseDTO, "Expected to return the correct ProductVariantResponseDTO");

        // Verify
        verify(productVariantRepository).findById(1L);
        verify(productVariantMapper).toResponseDTO(variant);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_VariantNotFound() {
        // Arrange
        when(productVariantRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> productVariantService.getProductVariantById(99L),
                "Expected ResourceNotFoundException when ProductVariant ID does not exist");

        // Verify
        verify(productVariantRepository).findById(99L);
        verifyNoMoreInteractions(productVariantRepository);
    }

    // ========================================
    // GET ALL
    // ========================================

    @Test
    void should_ReturnAllVariants() {
        // Arrange
        ProductVariant variant1 = new ProductVariant(product, size, 1000L, "ESP-SM-0001", 10L);
        ProductVariant variant2 = new ProductVariant(product, size, 1200L, "ESP-SM-0002", 20L);
        ProductVariantResponseDTO responseDTO1 = new ProductVariantResponseDTO(null, product.getId(), size.getName(), "ESP-SM-0001", 1000L, 10L);
        ProductVariantResponseDTO responseDTO2 = new ProductVariantResponseDTO(null, product.getId(), size.getName(), "ESP-SM-0002", 1200L, 20L);


        when(productVariantRepository.findAll()).thenReturn(List.of(variant1, variant2));
        when(productVariantMapper.toResponseDTO(variant1)).thenReturn(responseDTO1);
        when(productVariantMapper.toResponseDTO(variant2)).thenReturn(responseDTO2);

        // Act
        List<ProductVariantResponseDTO> result = productVariantService.getAllProductVariants();

        // Assert
        assertEquals(2, result.size());
        assertNotNull(result, "Result should not be null");
        assertEquals(responseDTO1, result.get(0), "Expected to return the correct ProductVariantResponseDTO");
        assertEquals(responseDTO2, result.get(1), "Expected to return the correct ProductVariantResponseDTO");

        // Verify
        verify(productVariantRepository).findAll();
        verify(productVariantMapper).toResponseDTO(variant1);
        verify(productVariantMapper).toResponseDTO(variant2);
        verifyNoMoreInteractions(productVariantRepository);
    }

    // ========================================
    // CREATE
    // ========================================

    @Test
    void should_CreateVariant_When_Valid() {
        // Arrange
        ProductVariantRequestDTO requestDTO = new ProductVariantRequestDTO(1L, 1L, 1200L, 20L);
        ProductVariant variant = new ProductVariant(product, size, 1200L, "ESP-SM-0002", 20L);
        ProductVariantResponseDTO expectedResponseDTO = new ProductVariantResponseDTO(null, product.getId(), size.getName(), "ESP-SM-0002", 1200L, 20L);

        when(productService.findById(1L)).thenReturn(product);
        when(sizeService.findById(1L)).thenReturn(size);
        when(productVariantRepository.existsByProductAndSize(product, size)).thenReturn(false);
        when(productVariantRepository.save(any(ProductVariant.class))).thenReturn(variant);
        when(productVariantMapper.toResponseDTO(variant)).thenReturn(expectedResponseDTO);

        // Act
        ProductVariantResponseDTO result = productVariantService.createProductVariant(requestDTO);

        // Assert
        assertNotNull(result, "Expected result to be not null");
        assertEquals(expectedResponseDTO, result, "Expected to return the correct ProductVariantResponseDTO");

        // Verify
        verify(productService).findById(1L);
        verify(sizeService).findById(1L);
        verify(productVariantRepository).existsByProductAndSize(product, size);
        verify(productVariantRepository).save(any(ProductVariant.class));
        verify(productVariantMapper).toResponseDTO(variant);
    }

    @Test
    void should_ThrowDuplicateResourceException_When_DuplicateVariantExists() {
        // Arrange
        ProductVariantRequestDTO dto = new ProductVariantRequestDTO(1L, 1L, 1200L, 20L);
        when(productService.findById(1L)).thenReturn(product);
        when(sizeService.findById(1L)).thenReturn(size);
        when(productVariantRepository.existsByProductAndSize(product, size)).thenReturn(true);

        // Act & Assert
        assertThrows(
                DuplicateResourceException.class,
                () -> productVariantService.createProductVariant(dto),
                "Expected DuplicateResourceException when ProductVariant already exists");

        // Verify
        verify(productVariantRepository).existsByProductAndSize(product, size);
        verifyNoMoreInteractions(productVariantRepository);
    }

    // ========================================
    // UPDATE
    // ========================================

    @Test
    void should_UpdateVariant_When_Valid() {
        // Arrange
        ProductVariantUpdateDTO dto = new ProductVariantUpdateDTO(1300L, 15L);
        ProductVariantResponseDTO expectedDTO = new ProductVariantResponseDTO(null, product.getId(), size.getName(), "ESP-SM-0001", 1300L, 15L);

        when(productVariantRepository.findById(1L)).thenReturn(Optional.of(variant));
        when(productVariantRepository.save(any())).thenReturn(variant);
        when(productVariantMapper.toResponseDTO(variant)).thenReturn(expectedDTO);

        // Act
        ProductVariantResponseDTO result = productVariantService.updateProductVariant(1L, dto);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(expectedDTO, result, "Expected updated ProductVariantResponseDTO to match");

        // Verify
        verify(productVariantRepository).findById(1L);
        verify(productVariantRepository).save(variant);
        verify(productVariantMapper).toResponseDTO(variant);
    }


    @Test
    void should_ThrowResourceNotFoundException_When_UpdatingNonexistentVariant() {
        // Arrange
        ProductVariantUpdateDTO dto = new ProductVariantUpdateDTO(1300L, 15L);
        when(productVariantRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productVariantService.updateProductVariant(99L, dto));

        // Verify
        verify(productVariantRepository).findById(99L);
    }

    // ========================================
    // DELETE
    // ========================================

    @Test
    void should_DeleteVariant_When_IdExists() {
        // Arrange
        when(productVariantRepository.findById(1L)).thenReturn(Optional.of(variant));

        // Act
        productVariantService.deleteProductVariant(1L);

        // Assert - no exception means success

        // Verify
        verify(productVariantRepository).findById(1L);
        verify(productVariantRepository).delete(variant);
    }

    @Test
    void should_DeleteVariantsBySizeId() {
        // Arrange
        Long sizeId = 1L;

        // Act
        productVariantService.deleteProductVariantBySizeId(sizeId);

        // Verify
        verify(productVariantRepository).deleteBySizeId(sizeId);
    }
}
