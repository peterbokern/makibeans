package com.makibeans.service;

import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.model.AttributeTemplate;
import com.makibeans.model.Category;
import com.makibeans.model.Product;
import com.makibeans.model.ProductAttribute;
import com.makibeans.repository.ProductAttributeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductAttributeServiceTest {

    Product product;
    AttributeTemplate attributeTemplate;

    @Mock
    ProductAttributeRepository productAttributeRepository;

    @Mock
    ProductService productService;

    @Mock
    AttributeTemplateService attributeTemplateService;

    @InjectMocks
    ProductAttributeService productAttributeService;

    @BeforeEach
    void setup() {
        product = new Product("productName", "productDescription", "productImageUrl", new Category("categoryName", "categoryDescription", "categoryImageUrl"));
        attributeTemplate = new AttributeTemplate("attributeTemplateName");
    }

    @AfterEach
    void tearDown(){
        product = null;
        attributeTemplate = null;
    }

    @Test
    void testCreateProductAttributeWithValidInput() {
        //arrange
        when(attributeTemplateService.findById(1L)).thenReturn(attributeTemplate);
        when(productService.findById(1L)).thenReturn(product);
        when(productAttributeRepository.existsByProductIdAndAttributeTemplateId(1L, 1L)).thenReturn(false);
        when(productAttributeRepository.save(any(ProductAttribute.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //act
        ProductAttribute result = productAttributeService.createProductAttribute(1L, 1L);

        //assert
        assertNotNull(result, "The created ProductAttribute should not be null.");
        assertEquals(result.getProduct(), product, "The Product in the ProductAttribute does not match the expected Product.");
        assertEquals(result.getAttributeTemplate(), attributeTemplate, "The AttributeTemplate in the ProductAttribute does not match the expected AttributeTemplate.");
        verify(attributeTemplateService).findById(eq(1L));
        verify(productService).findById(eq(1L));
        verify(productAttributeRepository).existsByProductIdAndAttributeTemplateId(eq(1L), eq(1L));
        verify(productAttributeRepository).save(result);
        verifyNoMoreInteractions(productAttributeRepository);
    }

    @Test
    void testCreateProductAttributeWithNullProductId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> productAttributeService.createProductAttribute(null, 1L));

        //verify
        verifyNoInteractions(productService, attributeTemplateService, productAttributeRepository);
    }

    @Test
    void testCreateProductAttributeWithNullTemplateId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> productAttributeService.createProductAttribute(1L, null));

        //verify
        verifyNoInteractions(productService, attributeTemplateService, productAttributeRepository);

    }

    @Test
    void testCreateProductAttributeWithInvalidProductId() {
        //arrange
        when(productService.findById(1L)).thenThrow(new ResourceNotFoundException(""));

        //act & assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> productAttributeService.createProductAttribute(1L, 1L),
                "Expected ResourceNotFoundException when trying to create a product attribute with an invalid attribute template ID.");

        //verify
        verify(productService).findById(any(Long.class));
        verifyNoInteractions(attributeTemplateService);
        verifyNoInteractions(productAttributeRepository);
    }

    @Test
    void testCreateProductAttributeWithInvalidAttributeTemplateId() {
        //arrange
        when(productService.findById(1L)).thenReturn(product);
        when(attributeTemplateService.findById(1L)).thenThrow(new ResourceNotFoundException(""));

        //act & assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> productAttributeService.createProductAttribute(1L, 1L),
                "Expected ResourceNotFoundException when trying to create a product attribute with an invalid product ID.");

        //verify
        verify(attributeTemplateService).findById(eq(1L));
        verifyNoInteractions(productAttributeRepository);
    }

    @Test
    void testCreateProductAttributeDuplicateResourceException() {
        //arrange
        when(productService.findById(1L)).thenReturn(product);
        when(attributeTemplateService.findById(1L)).thenReturn(attributeTemplate);
        when(productAttributeRepository.existsByProductIdAndAttributeTemplateId(1L, 1L)).thenReturn(true);

        //act & assert
        assertThrows(
                DuplicateResourceException.class,
                () -> productAttributeService.createProductAttribute(1L, 1L),
                "Expected DuplicateResourceException when trying to create a product attribute that already exists.");

        //verify
        verify(productService).findById(eq(1L));
        verify(attributeTemplateService).findById(eq(1L));
        verify(productAttributeRepository).existsByProductIdAndAttributeTemplateId(eq(1L), eq(1L));
        verifyNoMoreInteractions(productAttributeRepository, productService, attributeTemplateService);
    }

    @Test
    void testDeleteProductAttributeWithNullId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> productAttributeService.deleteProductAttribute(null),
                "Expected IllegalArgumentException when deleting a ProductAttribute with null ID.");

        //verify
        verifyNoInteractions(productAttributeRepository);
    }

    @Test
    void testDeleteProductAttributeTemplateWithValidProductAttributeId() {
        //arrange
        ProductAttribute productAttribute = new ProductAttribute(attributeTemplate, product);
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));

        //act
        productAttributeService.deleteProductAttribute(1L);

        //verify
        verify(productAttributeRepository).findById(eq(1L));
        verify(productAttributeRepository).delete(productAttribute);
        verifyNoMoreInteractions(productAttributeRepository);
    }

    @Test
    void testDeleteProductAttributeWithInvalidProductAttributeId() {
        // Arrange
        when(productAttributeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productAttributeService.deleteProductAttribute(99L),
                "Expected ResourceNotFoundException when deleting non-existent ProductAttribute.");

        // Verify
        verify(productAttributeRepository).findById(99L);
        verify(productAttributeRepository, never()).delete(any());
        verifyNoMoreInteractions(productAttributeRepository);
    }
}

