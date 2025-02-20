package com.makibeans.controller;

import com.makibeans.dto.AttributeTemplateRequestDTO;
import com.makibeans.dto.AttributeTemplateResponseDTO;
import com.makibeans.service.AttributeTemplateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attribute-templates")
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
    @GetMapping("/{id}")
    public ResponseEntity<AttributeTemplateResponseDTO> getAttributeTemplate(@PathVariable Long id) {
        AttributeTemplateResponseDTO responseDTO = attributeTemplateService.getAttributeTemplateById(id);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Retrieves all AttributeTemplates.
     *
     * @return the ResponseEntity containing the list of AttributeTemplateResponseDTOs
     */
    @GetMapping
    public ResponseEntity<List<AttributeTemplateResponseDTO>> getAllAttributeTemplates() {
        List<AttributeTemplateResponseDTO> responseDTOs = attributeTemplateService.getAllAttributeTemplates();
        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * Creates a new AttributeTemplate.
     *
     * @param dto the DTO containing the details of the AttributeTemplate to create
     * @return the ResponseEntity containing the created AttributeTemplateResponseDTO
     */
    @PostMapping
    public ResponseEntity<AttributeTemplateResponseDTO> createAttributeTemplate(
            @Valid @RequestBody AttributeTemplateRequestDTO dto) {
        AttributeTemplateResponseDTO createdDTO = attributeTemplateService.createAttributeTemplate(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDTO);
    }

    /**
     * Updates an existing AttributeTemplate.
     *
     * @param id  the ID of the AttributeTemplate to update
     * @param dto the DTO containing the updated details
     * @return the ResponseEntity containing the updated AttributeTemplateResponseDTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<AttributeTemplateResponseDTO> updateAttributeTemplate(
            @PathVariable Long id,
            @Valid @RequestBody AttributeTemplateRequestDTO dto) {
        AttributeTemplateResponseDTO updatedDTO = attributeTemplateService.updateAttributeTemplate(id, dto);
        return ResponseEntity.ok(updatedDTO);
    }

    /**
     * Deletes an AttributeTemplate by its ID.
     *
     * @param id the ID of the AttributeTemplate to delete
     * @return the ResponseEntity with appropriate HTTP status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttributeTemplate(@PathVariable Long id) {
        attributeTemplateService.deleteAttributeTemplate(id);
        return ResponseEntity.noContent().build();
    }
}
