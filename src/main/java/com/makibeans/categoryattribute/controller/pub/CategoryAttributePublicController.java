// language: java
package com.makibeans.categoryattribute.controller.pub;

import com.makibeans.categoryattribute.dto.CategoryAttributePublicResponseDTO;
import com.makibeans.categoryattribute.mapper.CategoryAttributeMapper;
import com.makibeans.categoryattribute.model.CategoryAttribute;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.CategoryAttributeFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.categoryattribute.service.CategoryAttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category-attributes")
@RequiredArgsConstructor
@Tag(name = "Category Attributes", description = "Public read API for category-attribute associations")
public class CategoryAttributePublicController {

    private final CategoryAttributeService service;
    private final CategoryAttributeMapper mapper;

    // ----------------------------- READS (public) -----------------------------

    @GetMapping("/{id}")
    @Operation(summary = "Get category-attribute by id (public)")
    public ResponseEntity<CategoryAttributePublicResponseDTO> getById(@PathVariable Long id) {
        CategoryAttribute entity = service.getById(id);
        return ResponseEntity.ok(mapper.toPublicResponseDTO(entity));
    }

    @GetMapping
    @Operation(
            summary = "Get category-attributes (paged, public)",
            description = "Search/sort/paginate category-attributes using query params."
    )
    public ResponseEntity<Page<CategoryAttributePublicResponseDTO>> getAll(
            @Valid @ModelAttribute CategoryAttributeFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<CategoryAttributeFilter> req =
                SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);

        Page<CategoryAttribute> page = service.search(req);
        Page<CategoryAttributePublicResponseDTO> result =
                page.map(mapper::toPublicResponseDTO);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/search")
    @Operation(
            summary = "Search category-attributes (POST, public)",
            description = "Same as GET but accepts a JSON body for complex filters."
    )
    public ResponseEntity<Page<CategoryAttributePublicResponseDTO>> search(
            @Valid @RequestBody SearchRequest<CategoryAttributeFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<CategoryAttributeFilter> merged =
                SearchRequestUtils.mergeWithPageable(request, pageable);

        if (merged.getIncludeDeleted() == null) {
            merged.setIncludeDeleted(false);
        }

        Page<CategoryAttribute> page = service.search(merged);
        Page<CategoryAttributePublicResponseDTO> result =
                page.map(mapper::toPublicResponseDTO);

        return ResponseEntity.ok(result);
    }
}
