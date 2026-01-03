// language: java
package com.makibeans.attribute.categoryattribute.controller.pub;

import com.makibeans.attribute.categoryattribute.dto.CategoryAttributePublicResponseDTO;
import com.makibeans.attribute.categoryattribute.filter.CategoryAttributeFilter;
import com.makibeans.attribute.categoryattribute.filter.CategoryAttributePublicFilter;
import com.makibeans.attribute.categoryattribute.mapper.CategoryAttributeMapper;
import com.makibeans.attribute.categoryattribute.model.CategoryAttribute;
import com.makibeans.attribute.categoryattribute.service.CategoryAttributeService;
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
            @Valid @ModelAttribute CategoryAttributePublicFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<CategoryAttributePublicFilter> req =
                SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);

        Page<CategoryAttribute> page = service.searchPublic(req);
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
            @Valid @RequestBody SearchRequest<CategoryAttributePublicFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<CategoryAttributePublicFilter> merged =
                SearchRequestUtils.mergeWithPageable(request, pageable);

        if (merged.getIncludeDeleted() == null) {
            merged.setIncludeDeleted(false);
        }

        Page<CategoryAttribute> page = service.searchPublic(merged);
        Page<CategoryAttributePublicResponseDTO> result =
                page.map(mapper::toPublicResponseDTO);

        return ResponseEntity.ok(result);
    }
}
