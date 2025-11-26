package com.makibeans.attribute.productattributevalue.controller.pub;

import com.makibeans.attribute.productattributevalue.filter.ProductAttributeValueFilter;
import com.makibeans.attribute.productattributevalue.dto.ProductAttributeValuePublicResponseDTO;
import com.makibeans.attribute.productattributevalue.mapper.ProductAttributeValueMapper;
import com.makibeans.attribute.productattributevalue.model.ProductAttributeValue;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.attribute.productattributevalue.service.ProductAttributeValueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public read-only API for ProductAttributeValue.
 */
@RestController
@RequestMapping("/product-attribute-values")
@RequiredArgsConstructor
@Tag(name = "Product Attribute Values", description = "Public read API for product attribute values")
public class ProductAttributeValuePublicController {

    private final ProductAttributeValueService service;
    private final ProductAttributeValueMapper mapper;

    /**
     * Get a product attribute value by id (public).
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product attribute value by ID (public)")
    public ResponseEntity<ProductAttributeValuePublicResponseDTO> getById(
            @PathVariable @NotNull Long id
    ) {
        ProductAttributeValue entity = service.getById(id);
        return ResponseEntity.ok(mapper.toPublicResponseDTO(entity));
    }


    // -------------------------------------------------------------------------
    // Paged GET with query params (public)
    // -------------------------------------------------------------------------

    @GetMapping
    @Operation(
            summary = "Get product attribute values (paged, public)",
            description = "Search/sort/paginate product attribute values using query parameters."
    )
    public ResponseEntity<Page<ProductAttributeValuePublicResponseDTO>> getAll(
            @Valid @ModelAttribute ProductAttributeValueFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductAttributeValueFilter> request =
                SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);

        Page<ProductAttributeValue> page = service.search(request);
        Page<ProductAttributeValuePublicResponseDTO> result =
                page.map(mapper::toPublicResponseDTO);

        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------------------------
    // POST /search with body (public)
    // -------------------------------------------------------------------------

    @PostMapping("/search")
    @Operation(
            summary = "Search product attribute values (public)",
            description = "Same as GET, but accepts a JSON body for complex filter structures."
    )
    public ResponseEntity<Page<ProductAttributeValuePublicResponseDTO>> search(
            @Valid @RequestBody SearchRequest<ProductAttributeValueFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductAttributeValueFilter> merged =
                SearchRequestUtils.mergeWithPageable(request, pageable);

        Page<ProductAttributeValue> page = service.search(merged);
        Page<ProductAttributeValuePublicResponseDTO> result =
                page.map(mapper::toPublicResponseDTO);

        return ResponseEntity.ok(result);
    }

}
