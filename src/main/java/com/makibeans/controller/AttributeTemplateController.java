package com.makibeans.controller;

import com.makibeans.dto.AttributeTemplateRequestDTO;
import com.makibeans.dto.AttributeTemplateResponseDTO;
import com.makibeans.service.AttributeTemplateService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AttributeTemplateController {

    private final AttributeTemplateService attributeTemplateService;

    public AttributeTemplateController(AttributeTemplateService attributeTemplateService) {
        this.attributeTemplateService = attributeTemplateService;
    }

    /**
     * Retrieves an AttributeTemplate by its ID.
     *
     * @param id the ID of the AttributeTemplate to retrieve
     * @return the ResponseEntity containing the AttributeTemplateResponseDTO
     */

    @GetMapping("/attribute-template/{id}")
    public ResponseEntity<AttributeTemplateResponseDTO> getAttributeTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(attributeTemplateService.findAttributeTemplateById(id));
    }

    /**
     * Retrieves all AttributeTemplates.
     *
     * @return the ResponseEntity containing the list of AttributeTemplateResponseDTOs
     */

    @GetMapping("/attribute-template")
    public ResponseEntity<List<AttributeTemplateResponseDTO>> findAllAttributeTemplates() {
        return ResponseEntity.ok(attributeTemplateService.findAllAttributeTemplates());
    }

    /**
     * Creates a new AttributeTemplate.
     *
     * @param dto the DTO containing the details of the AttributeTemplate to create
     * @return the ResponseEntity containing the created AttributeTemplateResponseDTO
     */

    @PostMapping("/attribute-template")
    public ResponseEntity<AttributeTemplateResponseDTO> addAttributeTemplate(@Valid @RequestBody AttributeTemplateRequestDTO dto) {
        return ResponseEntity.ok(attributeTemplateService.createAttributeTemplate(dto));
    }

    /**
     * Updates the  AttributeTemplate with given id.
     *
     * @param id the id of the AttributeTemplate to update.
     * @param dto the DTO containing the details of the AttributeTemplate to create
     * @return the ResponseEntity containing the created AttributeTemplateResponseDTO
     */

    @PutMapping("/attribute-template/{id}")
    public ResponseEntity<AttributeTemplateResponseDTO> updateAttributeTemplate(@PathVariable Long id, @Valid  @RequestBody AttributeTemplateRequestDTO dto) {
        return ResponseEntity.ok(attributeTemplateService.updateAttributeTemplate(id, dto));
    }
}