package com.makibeans.controller.pub;

import com.makibeans.dto.productattribute.ProductAttributePublicResponseDTO;
import com.makibeans.mapper.ProductAttributeMapper;
import com.makibeans.model.ProductAttribute;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.ProductAttributeFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.service.service.ProductAttributeService;
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
            @ModelAttribute ProductAttributeFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductAttributeFilter> req =
                SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);

        Page<ProductAttributePublicResponseDTO> result =
                service.search(req).map(mapper::toPublicResponseDTO);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<ProductAttributePublicResponseDTO>> search(
            @RequestBody SearchRequest<ProductAttributeFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductAttributeFilter> merged =
                SearchRequestUtils.mergeWithPageable(request, pageable);

        Page<ProductAttributePublicResponseDTO> result =
                service.search(merged).map(mapper::toPublicResponseDTO);

        return ResponseEntity.ok(result);
    }
}
