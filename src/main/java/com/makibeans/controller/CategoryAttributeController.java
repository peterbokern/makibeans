package com.makibeans.controller;

import com.makibeans.dto.categoryattribute.CategoryAttributeRequestDTO;
import com.makibeans.dto.categoryattribute.CategoryAttributeResponseDTO;
import com.makibeans.dto.categoryattribute.CategoryAttributeUpdateDTO;
import com.makibeans.search.filters.CategoryAttributeFilter;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.service.service.CategoryAttributeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Minimal, clean, scalable controller.
 * - Public reads (list, getById).
 * - Admin-only writes (create, update, delete).
 * - GET and POST /search share the same SearchRequest model.
 * - Simple merge of pageable/sort defaults to keep behavior identical.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/category-attributes")
public class CategoryAttributeController {

    private final CategoryAttributeService service;

    // ----------------------------- READS (public) -----------------------------

    /**
     * GET by id (simple read).
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryAttributeResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.geById(id));
    }

    /**
     * GET: paginated list with query params bound into the same Filter DTO used by POST.
     * Example: /api/category-attributes?categoryId=1,2&attributeName=origin&sort=id,desc&page=0&size=20
     */
    @GetMapping
    public ResponseEntity<Page<CategoryAttributeResponseDTO>> getAll(
            @Valid @ModelAttribute CategoryAttributeFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<CategoryAttributeFilter> req = SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        return ResponseEntity.ok(service.search(req));
    }

    /**
     * POST: JSON search with same SearchRequest used by GET.
     */
    @PostMapping("/search")
    public ResponseEntity<Page<CategoryAttributeResponseDTO>> search(
            @Valid @RequestBody SearchRequest<CategoryAttributeFilter> req,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<CategoryAttributeFilter> merged = SearchRequestUtils.mergeWithPageable(req, pageable);
        if (merged.getIncludeDeleted() == null) merged.setIncludeDeleted(false);
        return ResponseEntity.ok(service.search(merged));
    }

    // ----------------------------- WRITES (admin) -----------------------------

    /**
     * CREATE (admin).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryAttributeResponseDTO> create(
            @Valid @RequestBody CategoryAttributeRequestDTO body
    ) {
        CategoryAttributeResponseDTO created = service.create(body);
        return ResponseEntity.ok(created);
    }

    /**
     * UPDATE (admin) — full or partial depending on your service semantics.
     * If you support PATCH semantics, keep this as PUT for idempotent full update,
     * and add a separate @PatchMapping if you need it.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryAttributeResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryAttributeUpdateDTO body
    ) {
        CategoryAttributeResponseDTO updated = service.update(id, body);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE (admin) — either soft or hard delete as implemented in your service.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
