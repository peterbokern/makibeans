package com.makibeans.controller;

import com.makibeans.dto.SizeRequestDTO;
import com.makibeans.dto.SizeResponseDTO;
import com.makibeans.service.SizeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Sizes.
 * Provides endpoints for retrieving, creating, updating, and deleting sizes.
 */

@RestController
@RequestMapping("/sizes")
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

    @GetMapping("/{id}")
    public ResponseEntity<SizeResponseDTO> getSizeById(@PathVariable Long id) {
        SizeResponseDTO responseDTO = sizeService.getSizeById(id);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Retrieves all sizes.
     *
     * @return a ResponseEntity containing a list of SizeResponseDTOs
     */

    @GetMapping
    public ResponseEntity<List<SizeResponseDTO>> getAllSizes() {
        List<SizeResponseDTO> responseDTOS = sizeService.getAllSizes();
        return ResponseEntity.ok(responseDTOS);
    }

    /**
     * Creates a new size.
     *
     * @param requestDTO the SizeRequestDTO containing size details
     * @return a ResponseEntity containing the created SizeResponseDTO
     */

    @PostMapping
    public ResponseEntity<SizeResponseDTO> createSize(@Valid @RequestBody SizeRequestDTO requestDTO) {
        SizeResponseDTO responseDTO = sizeService.createSize(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Updates a size by its ID.
     *
     * @param id         the ID of the size to update
     * @param requestDTO the SizeRequestDTO containing updated size details
     * @return a ResponseEntity containing the updated SizeResponseDTO
     */

    @PutMapping("/{id}")
    public ResponseEntity<SizeResponseDTO> updateSize(@PathVariable Long id, @Valid @RequestBody SizeRequestDTO requestDTO) {
        SizeResponseDTO responseDTO = sizeService.updateSize(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Deletes a size by its ID.
     *
     * @param id the ID of the size to delete
     * @return a ResponseEntity indicating the result of the operation
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSize(@PathVariable Long id) {
        sizeService.deleteSize(id);
        return ResponseEntity.ok().build();
    }
}
