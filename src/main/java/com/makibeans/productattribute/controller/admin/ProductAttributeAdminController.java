package com.makibeans.productattribute.controller.admin;

import com.makibeans.productattribute.dto.ProductAttributeAdminResponseDTO;
import com.makibeans.productattribute.dto.ProductAttributeRequestDTO;
import com.makibeans.productattribute.dto.ProductAttributeUpdateDTO;
import com.makibeans.productattribute.filter.ProductAttributeAdminFilter;
import com.makibeans.productattribute.mapper.ProductAttributeMapper;
import com.makibeans.productattribute.model.ProductAttribute;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.productattribute.service.ProductAttributeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product-attributes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ProductAttributeAdminController {

    private final ProductAttributeService service;
    private final ProductAttributeMapper mapper;

    @GetMapping("/{id}")
    public ResponseEntity<ProductAttributeAdminResponseDTO> getById(@PathVariable Long id) {
        ProductAttribute entity = service.getById(id);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(entity));
    }

    @GetMapping
    public ResponseEntity<Page<ProductAttributeAdminResponseDTO>> getAll(
            @ModelAttribute ProductAttributeAdminFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductAttributeAdminFilter> req =
                SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);

        Page<ProductAttributeAdminResponseDTO> result =
                service.searchAdmin(req).map(mapper::toAdminResponseDTO);

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<ProductAttributeAdminResponseDTO> create(
            @Valid @RequestBody ProductAttributeRequestDTO body
    ) {
        ProductAttribute created = service.create(body);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toAdminResponseDTO(created));
    }

    @PatchMapping("/{id}/visible")
    @Operation(summary = "Admin: Update product attribute by id")
    public ResponseEntity<ProductAttributeAdminResponseDTO> patch(
            @PathVariable Long id,
            @Valid @RequestBody ProductAttributeUpdateDTO body
    ) {
        ProductAttribute updated = service.update(id, body);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Admin: Delete product attribute by id")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
