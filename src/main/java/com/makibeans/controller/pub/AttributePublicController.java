package com.makibeans.controller.pub;

import com.makibeans.dto.attribute.AttributePublicResponseDTO;
import com.makibeans.mapper.AttributeMapper;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.AttributeFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.service.service.AttributeService;
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
 * Attribute REST controller.
 * - GET accepts @ModelAttribute filters + search + includeDeleted + Pageable
 * - POST /search accepts a SearchRequest body
 * - Public reads, admin-protected writes
 */

@RestController
@RequestMapping("/attributes")
@RequiredArgsConstructor
@Tag(name = "Attributes", description = "Manage product attributes")
public class AttributePublicController {

    private final AttributeService service;
    private final AttributeMapper mapper;

    // ---------- READS ----------

    @GetMapping("/{id}")
    @Operation(summary = "Get attribute by id")
    public ResponseEntity<AttributePublicResponseDTO> getById(@PathVariable Long id) {
        AttributePublicResponseDTO response = mapper.toPublicResponseDTO(service.getById(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get attributes (paged)", description = "Search/sort/paginate attributes using query params.")
    public ResponseEntity<Page<AttributePublicResponseDTO>> getAll(
            @Valid @ModelAttribute AttributeFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributeFilter> req = SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        Page<AttributePublicResponseDTO> resultPage = service.search(req).map(mapper::toPublicResponseDTO);
        return ResponseEntity.ok(resultPage);
    }

    @PostMapping("/search")
    @Operation(summary = "Search attributes (POST)", description = "Same as GET but accepts a JSON body for complex filters.")
    public ResponseEntity<Page<AttributePublicResponseDTO>> search(
            @Valid @RequestBody SearchRequest<AttributeFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributeFilter> req = SearchRequestUtils.mergeWithPageable(request, pageable);
        Page<AttributePublicResponseDTO> resultPage = service.search(req).map(mapper::toPublicResponseDTO);
        return ResponseEntity.ok(resultPage);
    }
}
