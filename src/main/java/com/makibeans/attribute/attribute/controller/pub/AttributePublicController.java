
//DELETE

/*
package com.makibeans.attribute.attribute.controller.pub;

import com.makibeans.attribute.attribute.filter.AttributeFilter;
import com.makibeans.attribute.attribute.filter.AttributePublicFilter;
import com.makibeans.attribute.attribute.mapper.AttributeMapper;
import com.makibeans.attribute.attribute.service.AttributeService;
import com.makibeans.attribute.attribute.dto.AttributePublicResponseDTO;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.utils.SearchRequestUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

*/
/**
 * Attribute REST controller.
 * - GET accepts @ModelAttribute filters + search + includeDeleted + Pageable
 * - POST /search accepts a SearchRequest body
 * - Public reads, admin-protected writes
 *//*


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
            @Valid @ModelAttribute AttributePublicFilter filters,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributePublicFilter> req = SearchRequestUtils.assemble(filters, search, false, pageable);
        Page<AttributePublicResponseDTO> resultPage = service.searchPublic(req).map(mapper::toPublicResponseDTO);
        return ResponseEntity.ok(resultPage);
    }

    @PostMapping("/search")
    @Operation(summary = "Search attributes (POST)", description = "Same as GET but accepts a JSON body for complex filters.")
    public ResponseEntity<Page<AttributePublicResponseDTO>> search(
            @Valid @RequestBody SearchRequest<AttributePublicFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributePublicFilter> req = SearchRequestUtils.mergeWithPageable(request, pageable);
        req.setIncludeDeleted(false); // Public API should not include deleted
        Page<AttributePublicResponseDTO> resultPage = service.searchPublic(req).map(mapper::toPublicResponseDTO);
        return ResponseEntity.ok(resultPage);
    }
}
*/
