package com.makibeans.attribute.attributevalue.controller.pub;

import com.makibeans.attribute.attributevalue.dto.AttributeValuePublicResponseDTO;
import com.makibeans.attribute.attributevalue.filter.AttributeValuePublicFilter;
import com.makibeans.search.SearchRequest;
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
            @Valid @ModelAttribute AttributeValuePublicFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributeValuePublicFilter> req = SearchRequestUtils.assemble(filters, search, false, pageable);
        Page<AttributeValuePublicResponseDTO> page = service.searchPublic(req).map(mapper::toPublicResponseDTO);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/search")
    @Operation(summary = "Search attribute values (POST)", description = "Same as GET but accepts a JSON body for complex filters.")
    public ResponseEntity<Page<AttributeValuePublicResponseDTO>> search(
            @Valid @RequestBody SearchRequest<AttributeValuePublicFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributeValuePublicFilter> req = SearchRequestUtils.mergeWithPageable(request, pageable);
        req.setIncludeDeleted(false); // Public endpoint should not include deleted records
        Page<AttributeValuePublicResponseDTO> page = service.searchPublic(req).map(mapper::toPublicResponseDTO);
        return ResponseEntity.ok(page);
    }
}
