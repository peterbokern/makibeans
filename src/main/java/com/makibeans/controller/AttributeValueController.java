package com.makibeans.controller;

import com.makibeans.dto.*;
import com.makibeans.service.AttributeValueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Attribute Values.
 * Provides endpoints for retrieving, creating, updating, and deleting attribute values.
 */
@RestController
@RequestMapping("/attribute-values")
public class AttributeValueController {

    private final AttributeValueService attributeValueService;

    /**
     * Constructs an AttributeValueController with the given service.
     *
     * @param attributeValueService the service handling Attribute Value operations
     */
    public AttributeValueController(AttributeValueService attributeValueService) {
        this.attributeValueService = attributeValueService;
    }

    /**
     * Retrieves an AttributeValue by its unique identifier.
     *
     * @param id the unique identifier of the AttributeValue
     * @return a ResponseEntity containing the AttributeValueResponseDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<AttributeValueResponseDTO> getAttributeValue(@PathVariable Long id) {
        AttributeValueResponseDTO responseDTO = attributeValueService.getAttributeValueById(id);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Retrieves all AttributeValues.
     *
     * @return a ResponseEntity containing a list of AttributeValueResponseDTOs
     */
    @GetMapping
    public ResponseEntity<List<AttributeValueResponseDTO>> getAllAttributeValues() {
        List<AttributeValueResponseDTO> attributeValueResponseDTOS = attributeValueService.getAllAttributeValues();
        return ResponseEntity.ok(attributeValueResponseDTOS);
    }

    /**
     * Retrieves all AttributeValues associated with a given AttributeTemplate ID.
     *
     * @param templateId the ID of the AttributeTemplate
     * @return a ResponseEntity containing a list of AttributeValueResponseDTOs linked to the template
     */
    @GetMapping("/by-template-id/{templateId}")
    public ResponseEntity<List<AttributeValueResponseDTO>> getAllAttributeValuesByTemplateId(@PathVariable Long templateId) {
        List<AttributeValueResponseDTO> attributeValueResponseDTOS = attributeValueService.getAllAttributeValuesByTemplateId(templateId);
        return ResponseEntity.ok(attributeValueResponseDTOS);
    }

    /**
     * Creates a new AttributeValue.
     *
     * @param dto the DTO containing the details of the new AttributeValue
     * @return a ResponseEntity containing the created AttributeValueResponseDTO
     */

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AttributeValueResponseDTO> createAttributeValue(@Valid @RequestBody AttributeValueRequestDTO dto) {
        AttributeValueResponseDTO response = attributeValueService.createAttributeValue(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing AttributeValue.
     *
     * @param id  the ID of the AttributeValue to update
     * @param dto the DTO containing the updated details
     * @return a ResponseEntity containing the updated AttributeValueResponseDTO
     */

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AttributeValueResponseDTO> updateAttributeValue(
            @PathVariable Long id,
            @Valid @RequestBody AttributeValueUpdateDTO dto) {
        AttributeValueResponseDTO updatedDTO = attributeValueService.updateAttributeValue(id, dto);
        return ResponseEntity.ok(updatedDTO);
    }

    /**
     * Deletes an AttributeValue by its unique identifier.
     *
     * @param id the unique identifier of the AttributeValue to delete
     * @return a ResponseEntity with no content if the deletion was successful
     */

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttributeValue(@PathVariable Long id) {
        attributeValueService.deleteAttributeValue(id);
        return ResponseEntity.noContent().build();
    }
}
