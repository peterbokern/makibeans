package com.makibeans.controller;

import com.makibeans.dto.search.SearchRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.makibeans.dto.categoryattribute.CategoryAttributeRequestDTO;
import com.makibeans.dto.categoryattribute.CategoryAttributeResponseDTO;
import com.makibeans.dto.categoryattribute.CategoryAttributeUpdateDTO;
import com.makibeans.service.CategoryAttributeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category-attributes")
public class CategoryAttributeController {

    private final CategoryAttributeService service;

    // ---- READS ----
    @GetMapping
    @Operation(
            summary = "Retrieve all Category Attributes",
            description = "Fetches all Category Attributes without pagination or filters. "
                    + "Primarily for small lists or dropdowns in admin panels.")
    public ResponseEntity<List<CategoryAttributeResponseDTO>> getAll() {
        List<CategoryAttributeResponseDTO> result = service.getAll();
        return ResponseEntity.ok(result);
    }

    /**
     * Searches for category-attribute associations with pagination and filtering.
     *
     * @param req the \`SearchRequestDTO\` containing search parameters
     * @return a paginated list of matching \`CategoryAttributeResponseDTO\`s
     */

    @PostMapping("/search")
    @Operation(summary = "Search Category Attributes",
            description = "Performs a paginated and filtered search for Category Attributes using JSON body parameters.")
    public ResponseEntity<Page<CategoryAttributeResponseDTO>> search(
            @Valid @RequestBody SearchRequestDTO req) {

        Page<CategoryAttributeResponseDTO> result = service.search(req);
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves a specific category-attribute association by its ID.
     *
     * @param id the unique identifier of the category-attribute association
     * @return the matching \`CategoryAttributeResponseDTO\`
     */
    @GetMapping("/{id}")
    public CategoryAttributeResponseDTO get(@PathVariable Long id) {
        return service.getById(id);
    }

    // ---- WRITES ----

    /**
     * Creates a new category-attribute association.
     * Requires ADMIN role.
     *
     * @param dto the \`CategoryAttributeRequestDTO\` containing details of the association to create
     * @return the created \`CategoryAttributeResponseDTO\`
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryAttributeResponseDTO create(@Valid @RequestBody CategoryAttributeRequestDTO dto) {
        return service.create(dto);
    }

    /**
     * Updates an existing category-attribute association.
     * Requires ADMIN role.
     *
     * @param id  the unique identifier of the association to update
     * @param dto the \`CategoryAttributeUpdateDTO\` containing updated details
     * @return the updated \`CategoryAttributeResponseDTO\`
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryAttributeResponseDTO update(@PathVariable Long id,
                                               @Valid @RequestBody CategoryAttributeUpdateDTO dto) {
        return service.updateCategoryAttribute(id, dto);
    }

    /**
     * Deletes a category-attribute association by its ID.
     * Requires ADMIN role.
     *
     * @param id the unique identifier of the association to delete
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.deleteCategoryAttribute(id);
    }
}
