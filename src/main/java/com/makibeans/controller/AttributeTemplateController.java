package com.makibeans.controller;

import com.makibeans.dto.AttributeTemplateRequestDTO;
import com.makibeans.dto.AttributeTemplateResponseDTO;
import com.makibeans.model.AttributeTemplate;
import com.makibeans.service.AttributeTemplateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class AttributeTemplateController {

    private final AttributeTemplateService attributeTemplateService;

    public AttributeTemplateController(AttributeTemplateService attributeTemplateService) {
        this.attributeTemplateService = attributeTemplateService;
    }

    @PostMapping("/attribute-template")
    public ResponseEntity<AttributeTemplateResponseDTO> addAttributeTemplate(@Valid @RequestBody AttributeTemplateRequestDTO dto) {

        return ResponseEntity.ok((attributeTemplateService.createAttributeTemplate(dto)));
    }

}
