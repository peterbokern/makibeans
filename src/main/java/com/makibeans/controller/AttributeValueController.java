package com.makibeans.controller;

import com.makibeans.dto.attributevalue.AttributeValueRequestDTO;
import com.makibeans.dto.attributevalue.AttributeValueResponseDTO;
import com.makibeans.dto.attributevalue.AttributeValueUpdateDTO;
import com.makibeans.service.AttributeValueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing Attribute Values.
 * Provides endpoints for retrieving, creating, updating, and deleting attribute values.
 */
@RestController
@RequestMapping("/attribute-values")
@Tag(name = "Attribute Values", description = "CRUD operations for Attribute Values")
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
    @Operation(summary = "Get Attribute Value by ID")
    @GetMapping("/{id}")
    public ResponseEntity<AttributeValueResponseDTO> getAttributeValue(@PathVariable Long id) {
        AttributeValueResponseDTO responseDTO = attributeValueService.getAttributeValueById(id);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Retrieves all AttributeValues, or filters them based on search parameters.
     *
     * @param params optional search, sort, and order parameters
     * @return a ResponseEntity containing a list of AttributeValueResponseDTOs
     */
    @Operation(summary = "Get all or search Attribute Values",
            description = "Fetch attribute templates with optional filtering and sorting. " +
                    "Parameters include:\n" +
                    "- `search`: Partial match on the fields `name` or `attributeTemplate`.\n" +
                    "- `name`: Exact match on the attribute template name.\n" +
                    "- `sort`: Field to sort by (`id`, `name`).\n" +
                    "- `order`: Sort order (`asc`, `desc`).")
    @GetMapping
    public ResponseEntity<List<AttributeValueResponseDTO>> getAttributeValues(@RequestParam Map<String, String> params) {
        List<AttributeValueResponseDTO> attributeValueResponseDTOS = attributeValueService.findBySearchQuery(params);
        return ResponseEntity.ok(attributeValueResponseDTOS);
    }

    /**
     * Retrieves all AttributeValues associated with a given AttributeTemplate ID.
     *
     * @param templateId the ID of the AttributeTemplate
     * @return a ResponseEntity containing a list of AttributeValueResponseDTOs linked to the template
     */
    @Operation(summary = "Get all Attribute Values by Template ID")
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
    @Operation(summary = "Create a new Attribute Value")
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
    @Operation(summary = "Update an existing Attribute Value")
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
    @Operation(summary = "Delete an Attribute Value by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttributeValue(@PathVariable Long id) {
        attributeValueService.deleteAttributeValue(id);
        return ResponseEntity.noContent().build();
    }
}
