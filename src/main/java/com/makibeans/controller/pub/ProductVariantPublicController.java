package com.makibeans.controller.pub;

import com.makibeans.dto.productvariant.ProductVariantPublicResponseDTO;
import com.makibeans.mapper.ProductVariantMapper;
import com.makibeans.model.ProductVariant;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.ProductVariantFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.service.service.ProductVariantService;
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
            @Valid @ModelAttribute ProductVariantFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductVariantFilter> req =
                SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);

        Page<ProductVariant> page = service.search(req);
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
            @Valid @RequestBody SearchRequest<ProductVariantFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductVariantFilter> merged =
                SearchRequestUtils.mergeWithPageable(request, pageable);

        Page<ProductVariant> page = service.search(merged);
        Page<ProductVariantPublicResponseDTO> result = page.map(mapper::toPublicResponseDTO);

        return ResponseEntity.ok(result);
    }
}
