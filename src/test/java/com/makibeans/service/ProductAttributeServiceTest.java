package com.makibeans.service;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void testCreateProductAttributeValidInput() {
        //arrange
        when(attributeTemplateService.findById(1L)).thenReturn(attributeTemplate);
        when(productService.findById(1L)).thenReturn(product);
        when(productAttributeRepository.existsByProductIdAndAttributeTemplateId(1L, 1L)).thenReturn(false);
        when(productAttributeRepository.save(any(ProductAttribute.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //act
        ProductAttribute result = productAttributeService.createProductAttribute(1L, 1L);

        //assert
        assertNotNull(result, "The result should not be null");
        assertEquals(result.getProduct(), product);
        assertEquals(result.getAttributeTemplate(), attributeTemplate);
        verify(attributeTemplateService).findById(1L);
        verify(productService).findById(1L);
        verify(productAttributeRepository).existsByProductIdAndAttributeTemplateId(1L, 1L);
        verify(productAttributeRepository).save(result);
        verifyNoMoreInteractions(productAttributeRepository);
    }


}