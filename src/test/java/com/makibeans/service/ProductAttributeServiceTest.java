package com.makibeans.service;

import com.makibeans.dto.ProductAttributeRequestDTO;
import com.makibeans.dto.ProductAttributeResponseDTO;
import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.mapper.ProductAttributeMapper;
import com.makibeans.model.*;
import com.makibeans.repository.ProductAttributeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductAttributeServiceTest {

    Product product;
    AttributeTemplate attributeTemplate;
    AttributeValue attributeValue;
    ProductAttribute productAttribute;

    @Mock
    ProductAttributeRepository productAttributeRepository;

    @Mock
    ProductService productService;

    @Mock
    AttributeTemplateService attributeTemplateService;

    @Mock
    AttributeValueService attributeValueService;

    @Mock
    ProductAttributeMapper productAttributeMapper;

    @InjectMocks
    ProductAttributeService productAttributeService;

    @BeforeEach
    void setup() {
        product = new Product("productName", "productDescription", "productImageUrl",
                new Category("categoryName", "categoryDescription", "categoryImageUrl"));
        attributeTemplate = new AttributeTemplate("attributeTemplateName");
        attributeValue = new AttributeValue(attributeTemplate, "attributeValue");
        productAttribute = new ProductAttribute(attributeTemplate, product);
    }

    @AfterEach
    void tearDown() {
        product = null;
        attributeTemplate = null;
        attributeValue = null;
        productAttribute = null;
    }

    @Test
    void whenGetProductAttributeById_thenSuccess() {
        // Arrange
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        ProductAttributeResponseDTO expectedDTO = new ProductAttributeResponseDTO(1L, 1L, "productName", 1L, "attributeTemplateName", List.of());
        when(productAttributeMapper.toResponseDTO(productAttribute)).thenReturn(expectedDTO);

        // Act
        ProductAttributeResponseDTO result = productAttributeService.getProductAttributeById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result);

        // Verify
        verify(productAttributeRepository).findById(1L);
        verify(productAttributeMapper).toResponseDTO(productAttribute);
        verifyNoMoreInteractions(productAttributeRepository, productAttributeMapper);
    }

    @Test
    void whenGetProductAttributeByIdNotFound_thenThrowResourceNotFoundException() {
        // Arrange
        when(productAttributeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productAttributeService.getProductAttributeById(99L));

        // Verify
        verify(productAttributeRepository).findById(99L);
        verifyNoMoreInteractions(productAttributeRepository);
    }

    @Test
    void whenGetAllProductAttributes_thenSuccess() {
        // Arrange
        List<ProductAttribute> productAttributes = List.of(productAttribute);
        List<ProductAttributeResponseDTO> expectedDTOs = List.of(new ProductAttributeResponseDTO(1L, 1L, "productName", 1L, "attributeTemplateName", List.of()));
        when(productAttributeRepository.findAll()).thenReturn(productAttributes);
        when(productAttributeMapper.toResponseDTO(productAttribute)).thenReturn(expectedDTOs.get(0));

        // Act
        List<ProductAttributeResponseDTO> result = productAttributeService.getAllProductAttributes();

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTOs, result);

        // Verify
        verify(productAttributeRepository).findAll();
        verify(productAttributeMapper).toResponseDTO(productAttribute);
        verifyNoMoreInteractions(productAttributeRepository, productAttributeMapper);
    }

    @Test
    void whenCreateProductAttribute_thenSuccess() {
        // Arrange
        ProductAttributeRequestDTO requestDTO = new ProductAttributeRequestDTO(1L, 1L);
        when(attributeTemplateService.findById(1L)).thenReturn(attributeTemplate);
        when(productService.findById(1L)).thenReturn(product);
        when(productAttributeRepository.existsByProductIdAndAttributeTemplateId(1L, 1L)).thenReturn(false);
        when(productAttributeRepository.save(any(ProductAttribute.class))).thenReturn(productAttribute);
        ProductAttributeResponseDTO expectedDTO = new ProductAttributeResponseDTO();
        when(productAttributeMapper.toResponseDTO(any(ProductAttribute.class))).thenReturn(expectedDTO);

        // Act
        ProductAttributeResponseDTO result = productAttributeService.createProductAttribute(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result);

        // Verify
        verify(attributeTemplateService).findById(1L);
        verify(productService).findById(1L);
        verify(productAttributeRepository).existsByProductIdAndAttributeTemplateId(1L, 1L);
        verify(productAttributeRepository).save(any(ProductAttribute.class));
        verify(productAttributeMapper).toResponseDTO(any(ProductAttribute.class));
        verifyNoMoreInteractions(productAttributeRepository, attributeTemplateService, productService, productAttributeMapper);
    }

    @Test
    void whenCreateProductAttributeAndAlreadyExists_thenThrowDuplicateResourceException() {
        // Arrange
        ProductAttributeRequestDTO requestDTO = new ProductAttributeRequestDTO(1L, 1L);
        when(productAttributeRepository.existsByProductIdAndAttributeTemplateId(1L, 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> productAttributeService.createProductAttribute(requestDTO));

        // Verify
        verify(productAttributeRepository).existsByProductIdAndAttributeTemplateId(1L, 1L);
        verifyNoMoreInteractions(productAttributeRepository);
    }

    @Test
    void whenDeleteProductAttributeWithValidId_thenSuccess() {
        // Arrange
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));

        // Act
        productAttributeService.deleteProductAttribute(1L);

        // Verify
        verify(productAttributeRepository).findById(1L);
        verify(productAttributeRepository).delete(any(ProductAttribute.class));
        verifyNoMoreInteractions(productAttributeRepository);
    }

    @Test
    void whenDeleteProductAttributeWithInvalidId_thenThrowResourceNotFoundException() {
        // Arrange
        when(productAttributeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productAttributeService.deleteProductAttribute(99L));

        // Verify
        verify(productAttributeRepository).findById(99L);
        verifyNoMoreInteractions(productAttributeRepository);
    }

    @Test
    void whenAddAttributeValue_thenSuccess() {
        // Arrange
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        when(attributeValueService.findById(1L)).thenReturn(attributeValue);

        // Act
        productAttributeService.addAttributeValue(1L, 1L);

        // Verify
        verify(productAttributeRepository).findById(1L);
        verify(attributeValueService).findById(1L);
        verify(productAttributeRepository).save(any(ProductAttribute.class));
        verifyNoMoreInteractions(productAttributeRepository, attributeValueService);
    }

    @Test
    void whenAddAttributeValueAndProductAttributeNotFound_thenThrowResourceNotFoundException() {
        // Arrange
        when(productAttributeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productAttributeService.addAttributeValue(99L, 1L));

        // Verify
        verify(productAttributeRepository).findById(99L);
        verifyNoMoreInteractions(productAttributeRepository);
    }

    @Test
    void whenAddAttributeValueAndAttributeValueAlreadyExists_thenThrowDuplicateResourceException() {
        // Arrange
        productAttribute.getAttributeValues().add(attributeValue);
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        when(attributeValueService.findById(1L)).thenReturn(attributeValue);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> productAttributeService.addAttributeValue(1L, 1L));

        // Verify
        verify(productAttributeRepository).findById(1L);
        verify(attributeValueService).findById(1L);
        verifyNoMoreInteractions(productAttributeRepository, attributeValueService);
    }

    @Test
    void whenRemoveAttributeValue_thenSuccess() {
        // Arrange
        productAttribute.getAttributeValues().add(attributeValue);
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        when(attributeValueService.findById(1L)).thenReturn(attributeValue);

        // Act
        productAttributeService.removeAttributeValue(1L, 1L);

        // Verify
        verify(productAttributeRepository).findById(1L);
        verify(attributeValueService).findById(1L);
        verify(productAttributeRepository).save(any(ProductAttribute.class));
        verifyNoMoreInteractions(productAttributeRepository, attributeValueService);
    }

    @Test
    void whenRemoveAttributeValueAndProductAttributeNotFound_thenThrowResourceNotFoundException() {
        // Arrange
        when(productAttributeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productAttributeService.removeAttributeValue(99L, 1L));

        // Verify
        verify(productAttributeRepository).findById(99L);
        verifyNoMoreInteractions(productAttributeRepository);
    }

    @Test
    void whenRemoveAttributeValueAndAttributeValueNotFound_thenThrowResourceNotFoundException() {
        // Arrange
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        doThrow(new ResourceNotFoundException("AttributeValue with ID 99 not found"))
                .when(attributeValueService).findById(99L);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productAttributeService.removeAttributeValue(1L, 99L));

        // Verify
        verify(productAttributeRepository).findById(1L);
        verify(attributeValueService).findById(99L);
        verifyNoMoreInteractions(productAttributeRepository, attributeValueService);
    }

    @Test
    void whenRemoveAttributeValueAndAttributeValueNotAssociated_thenThrowResourceNotFoundException() {
        // Arrange
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        when(attributeValueService.findById(1L)).thenReturn(attributeValue);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productAttributeService.removeAttributeValue(1L, 1L));

        // Verify
        verify(productAttributeRepository).findById(1L);
        verify(attributeValueService).findById(1L);
        verifyNoMoreInteractions(productAttributeRepository, attributeValueService);
    }
}
