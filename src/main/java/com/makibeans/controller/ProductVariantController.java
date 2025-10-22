package com.makibeans.controller;

import com.makibeans.dto.productvariant.ProductVariantRequestDTO;
import com.makibeans.dto.productvariant.ProductVariantResponseDTO;
import com.makibeans.dto.productvariant.ProductVariantUpdateDTO;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.ProductVariantFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.service.service.ProductVariantService;
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
@RequestMapping("/api/product-variants")
@RequiredArgsConstructor
@Tag(name = "Product Variants", description = "Manage product variants")
public class ProductVariantController {

    private final ProductVariantService service;

    @GetMapping("/{id}")
    @Operation(summary = "Get variant by id")
    public ResponseEntity<ProductVariantResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get variants (paged)", description = "Search/sort/paginate variants using query params.")
    public ResponseEntity<Page<ProductVariantResponseDTO>> getAll(
            @Valid @ModelAttribute ProductVariantFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductVariantFilter> req = SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        return ResponseEntity.ok(service.search(req));
    }

    @PostMapping("/search")
    @Operation(summary = "Search variants (POST)")
    public ResponseEntity<Page<ProductVariantResponseDTO>> search(
            @Valid @RequestBody SearchRequest<ProductVariantFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductVariantFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        return ResponseEntity.ok(service.search(merged));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create variant")
    public ResponseEntity<ProductVariantResponseDTO> create(@Valid @RequestBody ProductVariantRequestDTO dto) {
        ProductVariantResponseDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update variant")
    public ResponseEntity<ProductVariantResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ProductVariantUpdateDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete variant")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Convenience actions


    @PostMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Set stock for the variant")
    public ResponseEntity<ProductVariantResponseDTO> setStock(@PathVariable Long id, @RequestParam Long stock) {
        return ResponseEntity.ok(service.setStock(id, stock));
    }

    @PostMapping("/{id}/stock/increment")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Increment stock for the variant")
    public ResponseEntity<ProductVariantResponseDTO> incrementStock(@PathVariable Long id, @RequestParam Long by) {
        return ResponseEntity.ok(service.incrementStock(id, by));
    }

    @PostMapping("/{id}/stock/decrement")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Decrement stock for the variant")
    public ResponseEntity<ProductVariantResponseDTO> decrementStock(@PathVariable Long id, @RequestParam Long by) {
        return ResponseEntity.ok(service.decrementStock(id, by));
    }
}
