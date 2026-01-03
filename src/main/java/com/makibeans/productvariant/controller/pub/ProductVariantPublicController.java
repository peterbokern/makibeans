/*
package com.makibeans.productvariant.controller.pub;

import com.makibeans.productvariant.dto.ProductVariantPublicResponseDTO;
import com.makibeans.productvariant.filter.ProductVariantPublicFilter;
import com.makibeans.productvariant.mapper.ProductVariantMapper;
import com.makibeans.productvariant.model.ProductVariant;
import com.makibeans.search.SearchRequest;
import com.makibeans.productvariant.filter.ProductVariantFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.productvariant.service.ProductVariantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product-variants")
@RequiredArgsConstructor
@Tag(name = "Product Variants", description = "Public read API for product variants")
public class ProductVariantPublicController {

    private final ProductVariantService service;
    private final ProductVariantMapper mapper;

    // -------------------------------------------------------------------------
    // GET by ID
    // -------------------------------------------------------------------------
    @GetMapping("/{id}")
    @Operation(summary = "Get product variant by id (public)")
    public ResponseEntity<ProductVariantPublicResponseDTO> getById(@PathVariable Long id) {
        ProductVariant entity = service.getById(id);
        return ResponseEntity.ok(mapper.toPublicResponseDTO(entity));
    }

    // -------------------------------------------------------------------------
    // GET (paged + filters)
    // -------------------------------------------------------------------------
    @GetMapping
    @Operation(
            summary = "Get product variants (paged, public)",
            description = "Search/sort/paginate product variants using query parameters."
    )
    public ResponseEntity<Page<ProductVariantPublicResponseDTO>> getAll(
            @Valid @ModelAttribute ProductVariantPublicFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductVariantPublicFilter> req =
                SearchRequestUtils.assemble(filters, search, false, pageable);

        Page<ProductVariant> page = service.searchPublic(req);
        Page<ProductVariantPublicResponseDTO> result = page.map(mapper::toPublicResponseDTO);

        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------------------------
    // POST /search
    // -------------------------------------------------------------------------
    @PostMapping("/search")
    @Operation(
            summary = "Search product variants (POST, public)",
            description = "Same as GET but accepts a JSON body for complex filters."
    )
    public ResponseEntity<Page<ProductVariantPublicResponseDTO>> search(
            @Valid @RequestBody SearchRequest<ProductVariantPublicFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductVariantPublicFilter> merged =
                SearchRequestUtils.mergeWithPageable(request, pageable);

        Page<ProductVariant> page = service.searchPublic(merged);
        Page<ProductVariantPublicResponseDTO> result = page.map(mapper::toPublicResponseDTO);

        return ResponseEntity.ok(result);
    }
}
*/
