package com.makibeans.service;

import com.makibeans.model.AttributeTemplate;
import com.makibeans.model.AttributeValue;
import com.makibeans.repository.AttributeTemplateRepository;
import com.makibeans.repository.AttributeValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class AttributeValueServiceTest {

    private AttributeTemplate attributeTemplate;

    @Mock
    private AttributeValueRepository attributeValueRepository;

    @Mock
    private AttributeTemplateRepository attributeTemplateRepository;

    @Mock
    private AttributeTemplateService attributeTemplateService;

    @InjectMocks
    private AttributeValueService attributeValueService;

    @BeforeEach
    void setUp() {
        openMocks(this);
        AttributeValue attributeValue;
        attributeTemplate = new AttributeTemplate("Origin");
    }

    //createAttributeValue tests
    @Test
    void testCreateAttributeValueWithValidValue() {
        //arrange
        when(attributeTemplateService.findById(1L)).thenReturn(attributeTemplate);
        when(attributeValueRepository.existsByValue(attributeTemplate, "Chili")).thenReturn(false);
        when(attributeValueRepository.save(any(AttributeValue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //act
        AttributeValue result = attributeValueService.createAttributeValue(1L, "Chili");

        //assert
        assertNotNull(result);
        assertEquals(attributeTemplate, result.getAttributeTemplate());
        assertEquals("Chili", result.getValue());
        verify(attributeTemplateService).findById(1L);
        verify(attributeValueRepository).existsByValue(any(AttributeTemplate.class),eq("Chili"));
        verify(attributeValueRepository).save(any(AttributeValue.class));
    }

    @Test
    void testCreateAttributeValueWithInvalidValue() {}

    @Test
    void testCreateAttributeValueWithDuplicateValue() {}

    // deleteAttributeValue tests
    @Test
    void testDeleteAttributeValue() {}

    @Test
    void testDeleteAttributeValueInvalidId() {}

    // updateAttributeValue tests
    @Test
    void testUpdateAttributeValueWithValidValue() {}

    @Test
    void testUpdateAttributeValueWithInvalidValue() {}

    @Test
    void testUpdateAttributeValueWithNonExistentId() {}
}