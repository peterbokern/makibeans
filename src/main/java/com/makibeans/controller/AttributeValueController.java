package com.makibeans.controller;

import com.makibeans.dto.attributevalue.AttributeValueRequestDTO;
import com.makibeans.dto.attributevalue.AttributeValueResponseDTO;
import com.makibeans.dto.attributevalue.AttributeValueUpdateDTO;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.AttributeValueFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.service.service.AttributeValueService;
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
 * AttributeValue REST controller.
 * Aligned with AttributeController:
 * - GET accepts @ModelAttribute filters + search + includeDeleted + Pageable
 * - POST /search accepts a SearchRequest body
 * - Public reads, admin-protected writes
 */
@RestController
@RequestMapping("/attribute-values")
@RequiredArgsConstructor
@Tag(name = "Attribute Values", description = "Manage attribute values")
public class AttributeValueController {

    private final AttributeValueService service;

    // ---------- READS ----------

    @GetMapping("/{id}")
    @Operation(summary = "Get attribute value by id")
    public ResponseEntity<AttributeValueResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get attribute values (paged)", description = "Search/sort/paginate attribute values using query params.")
    public ResponseEntity<Page<AttributeValueResponseDTO>> getAll(
            @Valid @ModelAttribute AttributeValueFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributeValueFilter> req = SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        return ResponseEntity.ok(service.search(req));
    }

    @PostMapping("/search")
    @Operation(summary = "Search attribute values (POST)", description = "Same as GET but accepts a JSON body for complex filters.")
    public ResponseEntity<Page<AttributeValueResponseDTO>> search(
            @Valid @RequestBody SearchRequest<AttributeValueFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributeValueFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        return ResponseEntity.ok(service.search(merged));
    }

    // ---------- WRITES (ADMIN) ----------

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create attribute value", description = "Admin only.")
    public ResponseEntity<AttributeValueResponseDTO> create(@Valid @RequestBody AttributeValueRequestDTO body) {
        // Keeping service naming aligned would prefer service.create(body)
        // but we call the current method to avoid breaking changes.
        AttributeValueResponseDTO created = service.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update attribute value", description = "Admin only. Full update semantics.")
    public ResponseEntity<AttributeValueResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody AttributeValueUpdateDTO body
    ) {
        AttributeValueResponseDTO updated = service.update(id, body);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Partially update attribute value", description = "Admin only. Partial update semantics.")
    public ResponseEntity<AttributeValueResponseDTO> patch(
            @PathVariable Long id,
            @Valid @RequestBody AttributeValueUpdateDTO body
    ) {
        // MapStruct ignores nulls, so we can reuse same update method
        AttributeValueResponseDTO updated = service.update(id, body);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete attribute value", description = "Admin only. Soft/hard delete per service implementation.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
