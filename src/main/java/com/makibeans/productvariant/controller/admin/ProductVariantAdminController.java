package com.makibeans.productvariant.controller.admin;

import com.makibeans.productvariant.dto.ProductVariantAdminResponseDTO;
import com.makibeans.productvariant.dto.ProductVariantRequestDTO;
import com.makibeans.productvariant.dto.ProductVariantUpdateDTO;
import com.makibeans.productvariant.mapper.ProductVariantMapper;
import com.makibeans.productvariant.model.ProductVariant;
import com.makibeans.search.SearchRequest;
import com.makibeans.productvariant.filter.ProductVariantAdminFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.productvariant.service.ProductVariantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product-variants")
@RequiredArgsConstructor
@Tag(name = "Product Variants (Admin)", description = "Admin operations for managing product variants")
@PreAuthorize("hasRole('ADMIN')")
public class ProductVariantAdminController {

    private final ProductVariantService service;
    private final ProductVariantMapper mapper;

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------
    @GetMapping("/{id}")
    @Operation(summary = "Admin: Get product variant by id")
    public ResponseEntity<ProductVariantAdminResponseDTO> getById(@PathVariable Long id) {
        ProductVariant entity = service.getById(id);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(entity));
    }

    @GetMapping
    @Operation(
            summary = "Admin: Get product variants (paged)",
            description = "Search/sort/paginate product variants using query parameters."
    )
    public ResponseEntity<Page<ProductVariantAdminResponseDTO>> getAll(
            @Valid @ModelAttribute ProductVariantAdminFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductVariantAdminFilter> req =
                SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);

        Page<ProductVariant> page = service.searchAdmin(req);
        Page<ProductVariantAdminResponseDTO> result = page.map(mapper::toAdminResponseDTO);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/search")
    @Operation(
            summary = "Admin: Search product variants (POST)",
            description = "Same as GET but accepts a JSON body for complex filters."
    )
    public ResponseEntity<Page<ProductVariantAdminResponseDTO>> search(
            @Valid @RequestBody SearchRequest<ProductVariantAdminFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductVariantAdminFilter> merged =
                SearchRequestUtils.mergeWithPageable(request, pageable);

        Page<ProductVariant> page = service.searchAdmin(merged);
        Page<ProductVariantAdminResponseDTO> result = page.map(mapper::toAdminResponseDTO);

        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------------------------
    // CREATE / UPDATE
    // -------------------------------------------------------------------------
    //add API instruction:
    //when a variant is created and if it's marked as default, other variants of the same product are updated to not be default.
    //if its the first variant for the product, it is automatically set as default.
    @PostMapping
    @Operation(summary = "Admin: Create product variant")
    public ResponseEntity<ProductVariantAdminResponseDTO> create(
            @Valid @RequestBody ProductVariantRequestDTO body
    ) {
        ProductVariant created = service.create(body);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toAdminResponseDTO(created));
    }

    //if a variant is updated to be default, other variants of the same product are updated to not be default.
    //if the default variant is unset, and there are other variants for the product, the first one is set as default.
    @PutMapping("/{id}")
    @Operation(summary = "Admin: Update product variant")
    public ResponseEntity<ProductVariantAdminResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductVariantUpdateDTO body
    ) {
        ProductVariant updated = service.update(id, body);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(updated));
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------
    @DeleteMapping("/{id}")
    @Operation(summary = "Admin: Delete product variant")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
