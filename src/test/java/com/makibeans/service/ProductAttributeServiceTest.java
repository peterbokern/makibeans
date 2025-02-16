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
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

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
        openMocks(this);
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
        verify(attributeTemplateService).findById(1L);
        verify(productService).findById(1L);
        verify(productAttributeRepository).existsByProductIdAndAttributeTemplateId(1L, 1L);
        verify(productAttributeRepository).save(result);
        verifyNoMoreInteractions(productAttributeRepository);
    }

    @Test
    void testCreateProductAttributeWithNullProductId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> productAttributeService.createProductAttribute(null, 1L));
    }

    @Test
    void testCreateProductAttributeWithNullTemplateId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> productAttributeService.createProductAttribute(1L, null));
    }

    @Test
    void testCreateProductAttributeWithInvalidProductId() {
        //arrange
        when(productService.findById(1L)).thenThrow(new ResourceNotFoundException(""));

        //act & assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> productAttributeService.createProductAttribute(1L, 1L),
                "Expected ResourceNotFoundException when trying to create a product attribute with an invalid product ID.");

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
        verify(attributeTemplateService).findById(any(Long.class));
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

}