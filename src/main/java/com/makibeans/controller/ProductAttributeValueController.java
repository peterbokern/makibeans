package com.makibeans.controller;

import com.makibeans.dto.productattributevalue.ProductAttributeValueRequestDTO;
import com.makibeans.dto.productattributevalue.ProductAttributeValueResponseDTO;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.ProductAttributeValueFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.service.service.ProductAttributeValueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing Product Attribute Values.
 * Provides endpoints for retrieving, creating, and deleting product attribute values.
 */
@RestController
@RequestMapping("/product-attribute-values")
@Tag(name = "Product Attribute Values", description = "Operations for managing product attribute values")
public class ProductAttributeValueController {

    private final ProductAttributeValueService service;

    public ProductAttributeValueController(ProductAttributeValueService productAttributeValueService) {
        this.service = productAttributeValueService;
    }

    /**
     * Retrieves a product attribute value by its ID.
     *
     * @param id the ID of the product attribute value to retrieve
     * @return a ResponseEntity containing the ProductAttributeValueResponseDTO
     */
    @Operation(summary = "Get product attribute value by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductAttributeValueResponseDTO> getById(@PathVariable Long id) {
        ProductAttributeValueResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(
            summary = "Get product attribute values (paged)",
            description = "Search/sort/paginate product attribute values using query params."
    )
    @GetMapping
    public ResponseEntity<Page<ProductAttributeValueResponseDTO>> getAll(
            @Valid @ModelAttribute ProductAttributeValueFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductAttributeValueFilter> req =
                SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        return ResponseEntity.ok(service.search(req));
    }

    @PostMapping("/search")
    @Operation(summary = "Search attribute values (POST)", description = "Same as GET but accepts a JSON body for complex filters.")
    public ResponseEntity<Page<ProductAttributeValueResponseDTO>> search(
            @Valid @RequestBody SearchRequest<ProductAttributeValueFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductAttributeValueFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        return ResponseEntity.ok(service.search(merged));
    }

    /**
     * Creates a new product attribute value.
     *
     * @param requestDTO the product attribute value to create
     * @return a ResponseEntity containing the created ProductAttributeValueResponseDTO
     */
    @Operation(summary = "Create a new product attribute value")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductAttributeValueResponseDTO> create(
            @Valid @RequestBody ProductAttributeValueRequestDTO requestDTO
    ) {
        ProductAttributeValueResponseDTO responseDTO = service.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Deletes a product attribute value by its ID.
     *
     * @param id the ID of the product attribute value to delete
     * @return a ResponseEntity indicating the result of the operation
     */
    @Operation(summary = "Delete a product attribute value by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
