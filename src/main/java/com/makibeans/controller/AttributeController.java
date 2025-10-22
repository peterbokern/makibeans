package com.makibeans.controller;

import com.makibeans.dto.attribute.AttributeRequestDTO;
import com.makibeans.dto.attribute.AttributeResponseDTO;
import com.makibeans.dto.attribute.AttributeUpdateDTO;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.AttributeFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.service.service.AttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Attribute REST controller.
 * Mirrors the pattern used in CategoryAttributeController:
 * - GET accepts @ModelAttribute filters + search + includeDeleted + Pageable
 * - POST /search accepts a SearchRequest body
 * - Public reads, admin-protected writes
 */
@RestController
@RequestMapping("/api/attributes")
@RequiredArgsConstructor
@Tag(name = "Attributes", description = "Manage product attributes")
public class AttributeController {

    private final AttributeService service;

    // ---------- READS ----------

    @GetMapping("/{id}")
    @Operation(summary = "Get attribute by id")
    public ResponseEntity<AttributeResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get attributes (paged)", description = "Search/sort/paginate attributes using query params.")
    public ResponseEntity<Page<AttributeResponseDTO>> getAll(
            @Valid @ModelAttribute AttributeFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributeFilter> req = SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        return ResponseEntity.ok(service.search(req));
    }

    @PostMapping("/search")
    @Operation(summary = "Search attributes (POST)", description = "Same as GET but accepts a JSON body for complex filters.")
    public ResponseEntity<Page<AttributeResponseDTO>> search(
            @Valid @RequestBody SearchRequest<AttributeFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributeFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        return ResponseEntity.ok(service.search(merged));
    }

    // ---------- WRITES (ADMIN) ----------

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create attribute", description = "Admin only.")
    public ResponseEntity<AttributeResponseDTO> create(@Valid @RequestBody AttributeRequestDTO body) {
        AttributeResponseDTO created = service.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update attribute", description = "Admin only. Full update semantics.")
    public ResponseEntity<AttributeResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody AttributeUpdateDTO body
    ) {
        AttributeResponseDTO updated = service.update(id, body);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Partially update attribute", description = "Admin only. Partial update semantics.")
    public ResponseEntity<AttributeResponseDTO> patch(
            @PathVariable Long id,
            @Valid @RequestBody AttributeUpdateDTO body
    ) {
        // MapStruct ignores nulls, so we can reuse the same service update method
        AttributeResponseDTO updated = service.update(id, body);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete attribute", description = "Admin only. Soft/hard delete per service implementation.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
