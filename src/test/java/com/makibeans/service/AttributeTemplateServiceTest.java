package com.makibeans.service;

import com.makibeans.model.AttributeTemplate;
import com.makibeans.repository.AttributeTemplateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class AttributeTemplateServiceTest {

    @Mock
    private AttributeTemplateRepository attributeTemplateRepository;

    @InjectMocks
    private AttributeTemplateService attributeTemplateService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //Test createAttributeTemplate
        //valid Name (not null/empty)
            //calls existByName() -> false -> calls create() -> return attributeTemplate
        //invalid name (null/empty) -> throw IllegalargumentException
        //duplicate name -> throw DuplicateResourceException

    @Test
    void testCreateAttributeTemplateWithValidNames() {
        //arrange
        //AttributeTemplateService should return false when attributeTemplateRepository.existsByName() is called
        //AttributeTemplateRepository should return a new AttributeTemplate when attributeTemplateRepository.save() is called
        Mockito.when(attributeTemplateRepository.existsByName(any(String.class))).thenReturn(false);
        Mockito.when(attributeTemplateRepository.save(any(AttributeTemplate.class)))
                .thenAnswer(invocation -> {
                    AttributeTemplate result = invocation.getArgument(0);
                    return new AttributeTemplate(result.getName());
                });

        //act createAttributeTemplate
        AttributeTemplate result = attributeTemplateService.createAttributeTemplate("Origin");

        //assert
        //not null
        assertNotNull(result, "The attribute template should not be null after creation");

        //correct name
        assertEquals("Origin", result.getName(), "The attribute template name should be correctly initialized by the constructor");

        //verify attributeTemplateRepository.existsByName() is called
        verify(attributeTemplateRepository).existsByName("Origin");

        //verify attributeTemplateRepository.create() is called
        verify(attributeTemplateRepository).save(any(AttributeTemplate.class));
    }


    @Test
    void testDeleteAttributeTemplate() {
    }

    @Test
    void testUpdateAttributeTemplate() {
    }
}