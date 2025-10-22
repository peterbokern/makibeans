package com.makibeans.controller;

import com.makibeans.dto.size.SizeRequestDTO;
import com.makibeans.dto.size.SizeResponseDTO;
import com.makibeans.dto.size.SizeUpdateDTO;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.SizeFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.service.service.SizeService;
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

@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
@Tag(name = "Sizes", description = "Manage sizes")
public class SizeController {

    private final SizeService service;

    @GetMapping("/{id}")
    @Operation(summary = "Get size by id")
    public ResponseEntity<SizeResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get sizes (paged)", description = "Search/sort/paginate sizes using query params.")
    public ResponseEntity<Page<SizeResponseDTO>> getAll(
            @Valid @ModelAttribute SizeFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<SizeFilter> req = SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        return ResponseEntity.ok(service.search(req));
    }

    @PostMapping("/search")
    @Operation(summary = "Search sizes (POST)", description = "Same as GET but accepts a JSON body for complex filters.")
    public ResponseEntity<Page<SizeResponseDTO>> search(
            @Valid @RequestBody SearchRequest<SizeFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<SizeFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        return ResponseEntity.ok(service.search(merged));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create size", description = "Admin only.")
    public ResponseEntity<SizeResponseDTO> create(@Valid @RequestBody SizeRequestDTO body) {
        SizeResponseDTO created = service.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update size", description = "Admin only.")
    public ResponseEntity<SizeResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody SizeUpdateDTO body
    ) {
        SizeResponseDTO updated = service.update(id, body);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete size", description = "Admin only.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
