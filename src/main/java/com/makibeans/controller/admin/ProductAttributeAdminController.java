package com.makibeans.controller.admin;

import com.makibeans.dto.productattribute.*;
import com.makibeans.mapper.ProductAttributeMapper;
import com.makibeans.model.ProductAttribute;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.ProductAttributeFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.service.service.ProductAttributeService;
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
            @ModelAttribute ProductAttributeFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductAttributeFilter> req =
                SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);

        Page<ProductAttributeAdminResponseDTO> result =
                service.search(req).map(mapper::toAdminResponseDTO);

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<ProductAttributeAdminResponseDTO> create(
            @RequestBody ProductAttributeRequestDTO body
    ) {
        ProductAttribute created = service.create(body);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toAdminResponseDTO(created));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
