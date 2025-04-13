package com.makibeans.controller;

import com.makibeans.dto.size.SizeRequestDTO;
import com.makibeans.dto.size.SizeResponseDTO;
import com.makibeans.dto.size.SizeUpdateDTO;
import com.makibeans.service.SizeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing Sizes.
 * Provides endpoints for retrieving, creating, updating, and deleting sizes.
 */
@RestController
@RequestMapping("/sizes")
@Tag(name = "Sizes", description = "Operations for managing available product sizes")
public class SizeController {

    private final SizeService sizeService;

    public SizeController(SizeService sizeService) {
        this.sizeService = sizeService;
    }

    /**
     * Retrieves a size by its ID.
     *
     * @param id the ID of the size to retrieve
     * @return a ResponseEntity containing the SizeResponseDTO
     */
    @Operation(summary = "Get size by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SizeResponseDTO> getSizeById(@PathVariable Long id) {
        SizeResponseDTO responseDTO = sizeService.getSizeById(id);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Retrieves all Sizes, or filters them based on search parameters.
     *
     * @param params optional search, sort, and order parameters
     * @return a ResponseEntity containing a list of SizeResponseDTOs
     */
    @Operation(
            summary = "Get all or search sizes",
            description = "Fetch sizes with optional filtering and sorting. " +
                    "Parameters include:\n" +
                    "- `search`: Partial match on size name.\n" +
                    "- `name`: Exact match on size name.\n" +
                    "- `sort`: Field to sort by (`id`, `name`).\n" +
                    "- `order`: Sort order (`asc`, `desc`).")
    @GetMapping
    public ResponseEntity<List<SizeResponseDTO>> getSizes(@RequestParam Map<String, String> params) {
        List<SizeResponseDTO> sizeResponseDTOS = sizeService.findBySearchQuery(params);
        return ResponseEntity.ok(sizeResponseDTOS);
    }

    /**
     * Creates a new size.
     *
     * @param requestDTO the SizeRequestDTO containing size details
     * @return a ResponseEntity containing the created SizeResponseDTO
     */
    @Operation(summary = "Create a new size")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SizeResponseDTO> createSize(@Valid @RequestBody SizeRequestDTO requestDTO) {
        SizeResponseDTO responseDTO = sizeService.createSize(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Updates a size by its ID.
     *
     * @param id         the ID of the size to update
     * @param updateDTO the SizeRequestDTO containing updated size details
     * @return a ResponseEntity containing the updated SizeResponseDTO
     */
    @Operation(summary = "Update size by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SizeResponseDTO> updateSize(
            @PathVariable Long id,
            @Valid @RequestBody SizeUpdateDTO updateDTO) {
        SizeResponseDTO responseDTO = sizeService.updateSize(id, updateDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Deletes a size by its ID.
     *
     * @param id the ID of the size to delete
     * @return a ResponseEntity indicating the result of the operation
     */
    @Operation(summary = "Delete size by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSize(@PathVariable Long id) {
        sizeService.deleteSize(id);
        return ResponseEntity.noContent().build();
    }
}
