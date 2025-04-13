package com.makibeans.controller;

import com.makibeans.dto.attributetemplate.AttributeTemplateRequestDTO;
import com.makibeans.dto.attributetemplate.AttributeTemplateResponseDTO;
import com.makibeans.dto.attributetemplate.AttributeTemplateUpdateDTO;
import com.makibeans.service.AttributeTemplateService;
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
@RequestMapping("/attribute-templates")
@Tag(name = "Attribute Templates", description = "CRUD operations for Attribute Templates")
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
    @Operation(summary = "Get Attribute Template by ID")
    @GetMapping("/{id}")
    public ResponseEntity<AttributeTemplateResponseDTO> getAttributeTemplate(@PathVariable Long id) {
        AttributeTemplateResponseDTO responseDTO = attributeTemplateService.getAttributeTemplateById(id);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Retrieves all AttributeTemplates or searches for AttributeTemplates based on the provided serach params.
     *
     * @param params the map containing the search parameters
     * @return the ResponseEntity containing the list of AttributeTemplateResponseDTOs
     */
    @Operation(summary = "Retrieve Attribute Templates",
            description = "Fetch attribute templates with optional filtering and sorting. " +
                    "Parameters include:\n" +
                    "- `search`: Partial text search for template names.\n" +
                    "- `name`: Exact match for a template name.\n" +
                    "- `sort`: Field to sort by (`id`, `name`).\n" +
                    "- `order`: Sort order (`asc`, `desc`).")
    @GetMapping
    public ResponseEntity<List<AttributeTemplateResponseDTO>> getTemplates(@RequestParam Map<String, String> params) {
        List<AttributeTemplateResponseDTO> attributeTemplateResponseDTOS =  attributeTemplateService.findBySearchQuery(params);
        return ResponseEntity.ok(attributeTemplateResponseDTOS);
    }

    /**
     * Creates a new AttributeTemplate.
     *
     * @param dto the DTO containing the details of the AttributeTemplate to create
     * @return the ResponseEntity containing the created AttributeTemplateResponseDTO
     */
    @Operation(summary = "Create a new Attribute Template")
    @PreAuthorize("hasRole('ADMIN')")
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
    @Operation(summary = "Update an existing Attribute Template")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AttributeTemplateResponseDTO> updateAttributeTemplate(
            @PathVariable Long id,
            @Valid @RequestBody AttributeTemplateUpdateDTO dto) {
        AttributeTemplateResponseDTO updatedDTO = attributeTemplateService.updateAttributeTemplate(id, dto);
        return ResponseEntity.ok(updatedDTO);
    }

    /**
     * Deletes an AttributeTemplate by its ID.
     *
     * @param id the ID of the AttributeTemplate to delete
     * @return the ResponseEntity with appropriate HTTP status
     */
    @Operation(summary = "Delete an Attribute Template by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttributeTemplate(@PathVariable Long id) {
        attributeTemplateService.deleteAttributeTemplate(id);
        return ResponseEntity.noContent().build();
    }
}
