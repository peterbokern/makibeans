/*
package com.makibeans.attribute.productattribute.controller.pub;

import com.makibeans.attribute.productattribute.dto.ProductAttributePublicResponseDTO;
import com.makibeans.attribute.productattribute.filter.ProductAttributeFilter;
import com.makibeans.attribute.productattribute.filter.ProductAttributePublicFilter;
import com.makibeans.attribute.productattribute.mapper.ProductAttributeMapper;
import com.makibeans.attribute.productattribute.model.ProductAttribute;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.attribute.productattribute.service.ProductAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product-attributes")
@RequiredArgsConstructor
public class ProductAttributePublicController {

    private final ProductAttributeService service;
    private final ProductAttributeMapper mapper;

    @GetMapping("/{id}")
    public ResponseEntity<ProductAttributePublicResponseDTO> getById(@PathVariable Long id) {
        ProductAttribute entity = service.getById(id);
        return ResponseEntity.ok(mapper.toPublicResponseDTO(entity));
    }

    @GetMapping
    public ResponseEntity<Page<ProductAttributePublicResponseDTO>> getAll(
            @ModelAttribute ProductAttributePublicFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductAttributePublicFilter> req =
                SearchRequestUtils.assemble(filters, search, false, pageable);

        req.getFilters().setVisible(true); // Only public (visible) attributes

        Page<ProductAttributePublicResponseDTO> result =
                service.searchPublic(req).map(mapper::toPublicResponseDTO);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<ProductAttributePublicResponseDTO>> search(
            @RequestBody SearchRequest<ProductAttributePublicFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductAttributePublicFilter> merged =
                SearchRequestUtils.mergeWithPageable(request, pageable);

        Page<ProductAttributePublicResponseDTO> result =
                service.searchPublic(merged).map(mapper::toPublicResponseDTO);

        return ResponseEntity.ok(result);
    }
}
*/
