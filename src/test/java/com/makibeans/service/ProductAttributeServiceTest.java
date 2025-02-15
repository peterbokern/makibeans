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



}