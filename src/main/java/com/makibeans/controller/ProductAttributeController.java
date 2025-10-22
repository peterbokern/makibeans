package com.makibeans.controller;

import com.makibeans.dto.productattribute.ProductAttributeRequestDTO;
import com.makibeans.dto.productattribute.ProductAttributeResponseDTO;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.ProductAttributeFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.service.service.ProductAttributeService;
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

import java.util.List;

/**
 * REST controller for managing Product Attributes.
 * Provides endpoints for retrieving, creating, updating, and deleting product attribute values.
 */
@RestController
@RequestMapping("/product-attributes")
@Tag(name = "Product Attributes", description = "Operations for managing product attributes and their values")
public class ProductAttributeController {

    private final ProductAttributeService service;

    public ProductAttributeController(ProductAttributeService productAttributeService) {
        this.service = productAttributeService;
    }

    /**
     * Retrieves a product attribute by its ID.
     *
     * @param id the ID of the product attribute to retrieve
     * @return a ResponseEntity containing the ProductAttributeResponseDTO
     */
    @Operation(summary = "Get product attribute by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductAttributeResponseDTO> productAttributeResponseDTO(@PathVariable Long id) {
        ProductAttributeResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(responseDTO);
    }


    @Operation(summary = "Get product attributes (paged)", description = "Search/sort/paginate product attributes using query params.")
    @GetMapping
    public ResponseEntity<Page<ProductAttributeResponseDTO>> getAll(
            @Valid @ModelAttribute ProductAttributeFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductAttributeFilter> req = SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        return ResponseEntity.ok(service.search(req));
    }

    @PostMapping("/search")
    @Operation(summary = "Search attributes (POST)", description = "Same as GET but accepts a JSON body for complex filters.")
    public ResponseEntity<Page<ProductAttributeResponseDTO>> search(
            @Valid @RequestBody SearchRequest<ProductAttributeFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductAttributeFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        return ResponseEntity.ok(service.search(merged));
    }

    /**
     * Creates a new product attribute.
     *
     * @param requestDTO the product attribute to create
     * @return a ResponseEntity containing the created ProductAttributeResponseDTO
     */
    @Operation(summary = "Create a new product attribute")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductAttributeResponseDTO> create(@Valid @RequestBody ProductAttributeRequestDTO requestDTO) {
        ProductAttributeResponseDTO responseDTO = service.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Deletes a product attribute by its ID.
     *
     * @param id the ID of the product attribute to delete
     * @return a ResponseEntity indicating the result of the operation
     */
    @Operation(summary = "Delete a product attribute by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
