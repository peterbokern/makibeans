package com.makibeans.productattributevalue.controller.admin;

import com.makibeans.productattributevalue.dto.ProductAttributeValueAdminResponseDTO;
import com.makibeans.productattributevalue.dto.ProductAttributeValueRequestDTO;
import com.makibeans.productattributevalue.mapper.ProductAttributeValueMapper;
import com.makibeans.productattributevalue.model.ProductAttributeValue;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.ProductAttributeValueFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.productattributevalue.service.ProductAttributeValueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product-attribute-values")
@RequiredArgsConstructor
@Tag(name = "Product Attribute Values (Admin)", description = "Admin operations for product attribute values")
@PreAuthorize("hasRole('ADMIN')")
public class ProductAttributeValueAdminController {

    private final ProductAttributeValueService service;
    private final ProductAttributeValueMapper mapper;

    // -------------------------------------------------------------------------
    // READ (single)
    // -------------------------------------------------------------------------

    @GetMapping("/{id}")
    @Operation(summary = "Admin: Get product attribute value by ID")
    public ResponseEntity<ProductAttributeValueAdminResponseDTO> getById(
            @PathVariable @NotNull Long id
    ) {
        ProductAttributeValue entity = service.getById(id);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(entity));
    }

    // -------------------------------------------------------------------------
    // READ (paged list via query parameters)
    // -------------------------------------------------------------------------

    @GetMapping
    @Operation(
            summary = "Admin: Get product attribute values (paged)",
            description = "Search/sort/paginate product attribute values using query parameters."
    )
    public ResponseEntity<Page<ProductAttributeValueAdminResponseDTO>> getAll(
            @Valid @ModelAttribute ProductAttributeValueFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductAttributeValueFilter> request =
                SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);

        Page<ProductAttributeValue> page = service.search(request);
        Page<ProductAttributeValueAdminResponseDTO> result =
                page.map(mapper::toAdminResponseDTO);

        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------------------------
    // READ (POST /search with JSON body)
    // -------------------------------------------------------------------------

    @PostMapping("/search")
    @Operation(
            summary = "Admin: Search product attribute values",
            description = "Same as GET, but accepts a JSON body for complex filters."
    )
    public ResponseEntity<Page<ProductAttributeValueAdminResponseDTO>> search(
            @Valid @RequestBody SearchRequest<ProductAttributeValueFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductAttributeValueFilter> merged =
                SearchRequestUtils.mergeWithPageable(request, pageable);

        Page<ProductAttributeValue> page = service.search(merged);
        Page<ProductAttributeValueAdminResponseDTO> result =
                page.map(mapper::toAdminResponseDTO);

        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @PostMapping
    @Operation(summary = "Admin: Create product attribute value")
    public ResponseEntity<ProductAttributeValueAdminResponseDTO> create(
            @Valid @RequestBody ProductAttributeValueRequestDTO body
    ) {
        ProductAttributeValue created = service.create(body);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toAdminResponseDTO(created));
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @DeleteMapping("/{id}")
    @Operation(summary = "Admin: Delete product attribute value by ID")
    public ResponseEntity<Void> delete(@PathVariable @NotNull Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
