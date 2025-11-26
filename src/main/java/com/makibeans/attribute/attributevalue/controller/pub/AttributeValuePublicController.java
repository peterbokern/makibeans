package com.makibeans.attribute.attributevalue.controller.pub;

import com.makibeans.attribute.attributevalue.dto.AttributeValuePublicResponseDTO;
import com.makibeans.search.SearchRequest;
import com.makibeans.attribute.attributevalue.filter.AttributeValueFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.attribute.attributevalue.service.AttributeValueService;
import com.makibeans.attribute.attributevalue.mapper.AttributeValueMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public AttributeValue REST controller.
 * Handles public operations for attribute values.
 */
@RestController
@RequestMapping("/attribute-values")
@RequiredArgsConstructor
@Tag(name = "Attribute Values", description = "Manage attribute values (public)")
public class AttributeValuePublicController {

    private final AttributeValueService service;
    private final AttributeValueMapper mapper;

    // ---------- READS ----------

    @GetMapping("/{id}")
    @Operation(summary = "Get attribute value by id")
    public ResponseEntity<AttributeValuePublicResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toPublicResponseDTO(service.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Get attribute values (paged)", description = "Search/sort/paginate attribute values using query params.")
    public ResponseEntity<Page<AttributeValuePublicResponseDTO>> getAll(
            @Valid @ModelAttribute AttributeValueFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributeValueFilter> req = SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        Page<AttributeValuePublicResponseDTO> page = service.search(req).map(mapper::toPublicResponseDTO);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/search")
    @Operation(summary = "Search attribute values (POST)", description = "Same as GET but accepts a JSON body for complex filters.")
    public ResponseEntity<Page<AttributeValuePublicResponseDTO>> search(
            @Valid @RequestBody SearchRequest<AttributeValueFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributeValueFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        Page<AttributeValuePublicResponseDTO> page = service.search(merged).map(mapper::toPublicResponseDTO);
        return ResponseEntity.ok(page);
    }
}
