package com.makibeans.controller;

import com.makibeans.dto.*;
import com.makibeans.service.AttributeValueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attribute-values")

public class AttributeValueController {

    private final AttributeValueService attributeValueService;

    public AttributeValueController(AttributeValueService attributeValueService) {
        this.attributeValueService = attributeValueService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttributeValueResponseDTO> getAttributeValue(@PathVariable Long id) {
        AttributeValueResponseDTO responseDTO = attributeValueService.getAttributeValueById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<AttributeValueResponseDTO>> getAllAttributeValues() {
        List<AttributeValueResponseDTO> attributeValueResponseDTOS = attributeValueService.getAllAttributeValues();
        return ResponseEntity.ok(attributeValueResponseDTOS);
    }

    @PostMapping
    public ResponseEntity<AttributeValueResponseDTO> createAttributeValue(@Valid @RequestBody AttributeValueRequestDTO dto) {
        AttributeValueResponseDTO response = attributeValueService.createAttributeValue(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing AttributeTemplate.
     *
     * @param id  the ID of the AttributeTemplate to update
     * @param dto the DTO containing the updated details
     * @return the ResponseEntity containing the updated AttributeTemplateResponseDTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<AttributeValueResponseDTO> updateAttributeValue(
            @PathVariable Long id,
            @Valid @RequestBody AttributeValueUpdateDTO dto) {
        AttributeValueResponseDTO updatedDTO = attributeValueService.updateAttributeValue(id, dto);
        return ResponseEntity.ok(updatedDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttributeValue(@PathVariable Long id) {
        attributeValueService.deleteAttributeValue(id);
        return ResponseEntity.noContent().build();
    }
}
