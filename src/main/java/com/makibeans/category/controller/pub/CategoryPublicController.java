package com.makibeans.category.controller.pub;

import com.makibeans.category.filter.dto.CategoryFilterDefinitionsResponseDTO;
import com.makibeans.category.dto.CategoryPublicResponseDTO;
import com.makibeans.category.dto.CategoryTreeResponseDTO;
import com.makibeans.category.mapper.CategoryMapper;
import com.makibeans.category.model.Category;
import com.makibeans.category.service.CategoryFilterService;
import com.makibeans.categoryattribute.dto.CategoryAttributeResponseDTO;
import com.makibeans.categoryattribute.mapper.CategoryAttributeMapper;
import com.makibeans.categoryattribute.model.CategoryAttribute;
import com.makibeans.categoryattribute.service.CategoryAttributeService;
import com.makibeans.search.SearchRequest;
import com.makibeans.category.filter.CategoryPublicFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Public Category REST controller.
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Public Categories", description = "Public operations for viewing categories")
public class CategoryPublicController {

    private final CategoryService service;
    private final CategoryMapper mapper;
    private final CategoryAttributeService categoryAttributeService;
    private final CategoryFilterService categoryFilterService;
    private final CategoryAttributeMapper categoryAttributeMapper;

    @GetMapping("/{id}")
    @Operation(summary = "Get category by id")
    public ResponseEntity<CategoryPublicResponseDTO> getById(@PathVariable Long id) {
        Category category = service.getById(id);
        return ResponseEntity.ok(mapper.toPublicResponseDTO(category));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get category by slug")
    public ResponseEntity<CategoryPublicResponseDTO> getBySlug(@PathVariable String slug) {
        Category category = service.getBySlug(slug);
        return ResponseEntity.ok(mapper.toPublicResponseDTO(category));
    }

    @GetMapping("/tree")
    @Operation(summary = "Get category tree for navigation")
    public ResponseEntity<List<CategoryTreeResponseDTO>> getTree() {
        List<Category> roots = service.getCategoryTree();
        return ResponseEntity.ok(mapper.toTreeDTOs(roots));
    }

    @GetMapping("{id}/filters")
    @Operation(summary = "Get filterable attributes for a category")
    public ResponseEntity<CategoryFilterDefinitionsResponseDTO> getCategoryFilters(@PathVariable Long id) {
        Category category = service.getById(id);
        return ResponseEntity.ok(categoryFilterService.getCategoryFilterDefinitions(category.getId()));
    }

    @GetMapping
    @Operation(summary = "Get categories (paged)", description = "Search/sort/paginate categories using query params.")
    public ResponseEntity<Page<CategoryPublicResponseDTO>> getAll(
            @Valid @ModelAttribute CategoryPublicFilter filters,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<CategoryPublicFilter> req = SearchRequestUtils.assemble(filters, search, false, pageable);
        Page<CategoryPublicResponseDTO> page = service.searchPublic(req).map(mapper::toPublicResponseDTO);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/search")
    @Operation(summary = "Search categories (POST)", description = "Same as GET but accepts a JSON body for complex filters.")
    public ResponseEntity<Page<CategoryPublicResponseDTO>> search(
            @Valid @RequestBody SearchRequest<CategoryPublicFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<CategoryPublicFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        Page<CategoryPublicResponseDTO> page = service.searchPublic(merged).map(mapper::toPublicResponseDTO);
        return ResponseEntity.ok(page);
    }

    //--------Category Attributes --------- ---//

    @GetMapping("/{id}/attributes/{attributeId}")
    @Operation(summary = "Get category-attributes by category id")
    public ResponseEntity<CategoryAttributeResponseDTO> getCategoryAttributeLink(@PathVariable Long id, @PathVariable Long attributeId) {
        CategoryAttribute attribute = categoryAttributeService.getByCategoryIdAndAttributeIdIncludingDeleted(id, attributeId);
        CategoryAttributeResponseDTO mappedAttribute = categoryAttributeMapper.toResponseDTO(attribute);
        return ResponseEntity.ok(mappedAttribute);
    }

    @GetMapping("/{id}/attributes")
    @Operation(summary = "Admin: Get category-attributes by category id")
    public ResponseEntity<List<CategoryAttributeResponseDTO>> getAllCategoryAttributeLinks(@PathVariable Long id) {
        service.getById(id); // Ensure category exists
        List<CategoryAttribute> attributes = categoryAttributeService.getAllByCategoryId(id);
        List<CategoryAttributeResponseDTO> mappedAttributes = attributes.stream()
                .map(categoryAttributeMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(mappedAttributes);
    }
}