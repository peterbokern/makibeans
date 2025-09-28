package com.makibeans.controller;

import com.makibeans.dto.attribute.AttributeRequestDTO;
import com.makibeans.dto.attribute.AttributeResponseDTO;
import com.makibeans.dto.attribute.AttributeUpdateDTO;
import com.makibeans.service.AttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attributes")
@Tag(name = "Attribute", description = "CRUD operations for Attribute Templates")
public class AttributeController {

    private final AttributeService attributeService;

    public AttributeController(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    /**
     * Retrieves an Attribute by its ID.
     *
     * @param id the ID of the Attribute to retrieve
     * @return the ResponseEntity containing the AttributeTemplateResponseDTO
     */
    @Operation(summary = "Get Attribute by ID")
    @GetMapping("/{id}")
    public ResponseEntity<AttributeResponseDTO> getAttribute(@PathVariable Long id) {
        AttributeResponseDTO responseDTO = attributeService.getAttributeById(id);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Retrieves all AttributeTemplates or searches for AttributeTemplates based on the provided serach params.
     *
     * @param params the map containing the search parameters
     * @return the ResponseEntity containing the list of AttributeTemplateResponseDTOs
     */
    @Operation(summary = "Retrieve Attributes",
            description = "Fetch attributes with optional filtering and sorting. " +
                    "Parameters include:\n" +
                    "- `search`: Partial text search for template names.\n" +
                    "- `name`: Exact match for a template name.\n" +
                    "- `sort`: Field to sort by (`id`, `name`).\n" +
                    "- `order`: Sort order (`asc`, `desc`).")
    @GetMapping
    public ResponseEntity<List<AttributeResponseDTO>> getAttributes(@RequestParam Map<String, String> params) {
        List<AttributeResponseDTO> attributeTemplateResponseDTOS =  attributeService.findBySearchQuery(params);
        return ResponseEntity.ok(attributeTemplateResponseDTOS);
    }

    /**
     * Creates a new Attribute.
     *
     * @param dto the DTO containing the details of the Attribute to create
     * @return the ResponseEntity containing the created AttributeTemplateResponseDTO
     */
    @Operation(summary = "Create a new Attribute Template")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AttributeResponseDTO> createAttribute(
            @Valid @RequestBody AttributeRequestDTO dto) {
        AttributeResponseDTO createdDTO = attributeService.createAttribute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDTO);
    }

    /**
     * Updates an existing Attribute.
     *
     * @param id  the ID of the Attribute to update
     * @param dto the DTO containing the updated details
     * @return the ResponseEntity containing the updated AttributeTemplateResponseDTO
     */
    @Operation(summary = "Update an existing Attribute Template")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AttributeResponseDTO> updateAttribute(
            @PathVariable Long id,
            @Valid @RequestBody AttributeUpdateDTO dto) {
        AttributeResponseDTO updatedDTO = attributeService.updateAttribute(id, dto);
        return ResponseEntity.ok(updatedDTO);
    }

    /**
     * Deletes an Attribute by its ID.
     *
     * @param id the ID of the Attribute to delete
     * @return the ResponseEntity with appropriate HTTP status
     */
    @Operation(summary = "Delete an Attribute Template by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttribute(@PathVariable Long id) {
        attributeService.deleteAttribute(id);
        return ResponseEntity.noContent().build();
    }
}
