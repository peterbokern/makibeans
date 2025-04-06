package com.makibeans.service;

import com.makibeans.dto.productattribute.ProductAttributeRequestDTO;
import com.makibeans.dto.productattribute.ProductAttributeResponseDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.mapper.ProductAttributeMapper;
import com.makibeans.model.*;
import com.makibeans.repository.ProductAttributeRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductAttributeServiceTest {

    Product product;
    AttributeTemplate attributeTemplate;
    AttributeValue attributeValue;
    ProductAttribute productAttribute;

    @Mock ProductAttributeRepository productAttributeRepository;
    @Mock ProductService productService;
    @Mock AttributeTemplateService attributeTemplateService;
    @Mock AttributeValueService attributeValueService;
    @Mock ProductAttributeMapper productAttributeMapper;

    @InjectMocks ProductAttributeService productAttributeService;

    @BeforeEach
    void setup() {
        Category category = new Category("categoryName", "categoryDescription");
        product = new Product("productName", "productDescription", null, category);
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
    void should_ReturnProductAttribute_When_IdExists() {
        // Arrange
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        ProductAttributeResponseDTO expectedDTO = new ProductAttributeResponseDTO(1L, 1L, "attributeTemplateName", List.of());
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
    void should_ThrowResourceNotFoundException_When_ProductAttributeNotFoundById() {
        // Arrange
        when(productAttributeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> productAttributeService.getProductAttributeById(99L),
                "Expected ResourceNotFoundException when product attribute is not found by ID");

        // Verify
        verify(productAttributeRepository).findById(99L);
        verifyNoMoreInteractions(productAttributeRepository);
    }

    @Test
    void should_ReturnAllProductAttributes() {
        // Arrange
        ProductAttributeResponseDTO expectedResponseDTO = new ProductAttributeResponseDTO(1L, 1L, "Ethiopia", List.of());
        when(productAttributeRepository.findAll()).thenReturn(List.of(productAttribute));
        when(productAttributeMapper.toResponseDTO(productAttribute)).thenReturn(expectedResponseDTO);

        // Act
        List<ProductAttributeResponseDTO> result = productAttributeService.getAllProductAttributes();

        // Assert
        assertNotNull(result, "Result list should not be null");
        assertEquals(1, result.size(), "Result list should contain exactly 1 element");
        assertEquals(expectedResponseDTO, result.get(0), "The first ProductAttributeResponseDTO should match the expected");

        // Verify
        verify(productAttributeRepository).findAll();
        verify(productAttributeMapper).toResponseDTO(productAttribute);
        verifyNoMoreInteractions(productAttributeRepository, productAttributeMapper);
    }

    @Test
    void should_CreateProductAttribute_When_Valid() {
        // Arrange
        ProductAttributeRequestDTO requestDTO = new ProductAttributeRequestDTO(1L, 1L);
        ProductAttributeResponseDTO expectedResponseDTO = new ProductAttributeResponseDTO(1L, 1L, "Origin", List.of());

        when(productService.findById(1L)).thenReturn(product);
        when(attributeTemplateService.findById(1L)).thenReturn(attributeTemplate);
        when(productAttributeRepository.existsByProductIdAndAttributeTemplateId(1L, 1L)).thenReturn(false);
        when(productAttributeRepository.save(any())).thenReturn(productAttribute);
        when(productAttributeMapper.toResponseDTO(productAttribute)).thenReturn(expectedResponseDTO);

        // Act
        ProductAttributeResponseDTO result = productAttributeService.createProductAttribute(requestDTO);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(expectedResponseDTO, result, "Returned ProductAttributeResponseDTO should match the expected value");

        // Verify
        verify(productService).findById(1L);
        verify(attributeTemplateService).findById(1L);
        verify(productAttributeRepository).existsByProductIdAndAttributeTemplateId(1L, 1L);
        verify(productAttributeRepository).save(any());
        verify(productAttributeMapper).toResponseDTO(productAttribute);
        verifyNoMoreInteractions(productAttributeRepository, productService, attributeTemplateService, productAttributeMapper);
    }


    @Test
    void should_ThrowDuplicateResourceException_When_ProductAttributeAlreadyExists() {
        // Arrange
        when(productAttributeRepository.existsByProductIdAndAttributeTemplateId(1L, 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> productAttributeService.createProductAttribute(new ProductAttributeRequestDTO(1L, 1L)));

        // Verify
        verify(productAttributeRepository).existsByProductIdAndAttributeTemplateId(1L, 1L);
        verifyNoMoreInteractions(productAttributeRepository);
    }

    @Test
    void should_DeleteProductAttribute_When_IdExists() {
        // Arrange
        ReflectionTestUtils.setField(productAttribute, "id", 1L);
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));

        // Act
        productAttributeService.deleteProductAttribute(1L);

        // Verify
        verify(productAttributeRepository, times(2)).findById(1L);
        verify(productAttributeRepository).deleteAttributeValuesByProductAttributeId(1L);
        verify(productAttributeRepository).delete(productAttribute);
        verifyNoMoreInteractions(productAttributeRepository);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_DeletingNonexistentProductAttribute() {
        // Arrange
        when(productAttributeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> productAttributeService.deleteProductAttribute(99L),
                "Expected ResourceNotFoundException when product attribute is not found by ID");

        // Verify
        verify(productAttributeRepository).findById(99L);
        verifyNoMoreInteractions(productAttributeRepository);
    }

    @Test
    void should_AddAttributeValue_When_NotAlreadyAssociated() {
        // Arrange
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        when(attributeValueService.findById(1L)).thenReturn(attributeValue);

        // Act
        productAttributeService.addAttributeValue(1L, 1L);

        // Assert
        assertTrue(productAttribute.getAttributeValues().contains(attributeValue), "AttributeValue should be added to ProductAttribute");

        // Verify
        verify(productAttributeRepository).findById(1L);
        verify(attributeValueService).findById(1L);
        verify(productAttributeRepository).save(productAttribute);
        verifyNoMoreInteractions(productAttributeRepository, attributeValueService);
    }

    @Test
    void should_ThrowDuplicateResourceException_When_AttributeValueAlreadyAssociated() {
        // Arrange
        productAttribute.getAttributeValues().add(attributeValue);
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        when(attributeValueService.findById(1L)).thenReturn(attributeValue);

        // Act & Assert
        assertThrows(
                DuplicateResourceException.class,
                () -> productAttributeService.addAttributeValue(1L, 1L),
                "Expected DuplicateResourceException when AttributeValue is already associated with ProductAttribute");

        // Verify
        verify(productAttributeRepository).findById(1L);
        verify(attributeValueService).findById(1L);
        verifyNoMoreInteractions(productAttributeRepository, attributeValueService);
    }

    @Test
    void should_RemoveAttributeValue_When_Associated() {
        // Arrange
        productAttribute.getAttributeValues().add(attributeValue);
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        when(attributeValueService.findById(1L)).thenReturn(attributeValue);

        // Act
        productAttributeService.removeAttributeValue(1L, 1L);

        // Assert
        assertFalse(productAttribute.getAttributeValues().contains(attributeValue), "AttributeValue should be removed from ProductAttribute");

        // Verify
        verify(productAttributeRepository).findById(1L);
        verify(attributeValueService).findById(1L);
        verify(productAttributeRepository).save(productAttribute);
        verifyNoMoreInteractions(productAttributeRepository, attributeValueService);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_AttributeValueNotAssociated() {
        // Arrange
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        when(attributeValueService.findById(1L)).thenReturn(attributeValue);

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> productAttributeService.removeAttributeValue(1L, 1L),
                "Expected ResourceNotFoundException when AttributeValue is not associated with ProductAttribute");

        // Verify
        verify(productAttributeRepository).findById(1L);
        verify(attributeValueService).findById(1L);
        verifyNoMoreInteractions(productAttributeRepository, attributeValueService);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_AttributeValueToRemoveNotFound() {
        // Arrange
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        when(attributeValueService.findById(99L)).thenThrow(new ResourceNotFoundException("Not found"));

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> productAttributeService.removeAttributeValue(1L, 99L),
                "Expected ResourceNotFoundException when AttributeValue to remove is not found");

        // Verify
        verify(productAttributeRepository).findById(1L);
        verify(attributeValueService).findById(99L);
        verifyNoMoreInteractions(productAttributeRepository, attributeValueService);
    }

    @Test
    void should_ReturnProductAttributes_ByTemplateId() {
        // Arrange
        when(productAttributeRepository.findByAttributeTemplateId(1L)).thenReturn(List.of(productAttribute));

        // Act
        List<ProductAttribute> result = productAttributeService.getProductAttributesByTemplateId(1L);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Expected one ProductAttribute in the result list");
        assertEquals(productAttribute, result.get(0), "Returned ProductAttribute should match the expected one");

        // Verify
        verify(productAttributeRepository).findByAttributeTemplateId(1L);
        verifyNoMoreInteractions(productAttributeRepository);
    }

    @Test
    void should_ReturnProductAttributes_ByProductId() {
        // Arrange
        when(productAttributeRepository.findByProductId(1L)).thenReturn(List.of(productAttribute));

        // Act
        List<ProductAttribute> result = productAttributeService.getProductAttributesByProductId(1L);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Expected one ProductAttribute in the result list");
        assertEquals(productAttribute, result.get(0), "Returned ProductAttribute should match the expected one");

        // Verify
        verify(productAttributeRepository).findByProductId(1L);
        verifyNoMoreInteractions(productAttributeRepository);
    }

    @Test
    void should_DeleteAttributeValues_ByAttributeValueId() {
        // Act
        productAttributeService.deleteAttributeValuesByAttributeValueId(1L);

        // Verify
        verify(productAttributeRepository).deleteAttributeValuesByAttributeValueId(1L);
        verifyNoMoreInteractions(productAttributeRepository);
    }
}
