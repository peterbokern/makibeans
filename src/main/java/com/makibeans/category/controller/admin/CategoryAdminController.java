package com.makibeans.category.controller.admin;

import com.makibeans.category.dto.CategoryAdminResponseDTO;
import com.makibeans.category.dto.CategoryRequestDTO;
import com.makibeans.category.dto.CategoryUpdateDTO;
import com.makibeans.category.mapper.CategoryMapper;
import com.makibeans.category.model.Category;
import com.makibeans.categoryattribute.dto.*;
import com.makibeans.categoryattribute.mapper.CategoryAttributeMapper;
import com.makibeans.categoryattribute.model.CategoryAttribute;
import com.makibeans.categoryattribute.service.CategoryAttributeService;
import com.makibeans.search.SearchRequest;
import com.makibeans.category.filter.CategoryAdminFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/categories")
@Tag(name = "Admin Categories", description = "Admin operations for managing categories")
public class CategoryAdminController {

    private final CategoryService service;
    private final CategoryMapper mapper;
    private final CategoryAttributeService categoryAttributeService;
    private final CategoryAttributeMapper categoryAttributeMapper;

    public CategoryAdminController(CategoryService service, CategoryMapper mapper, CategoryAttributeService categoryAttributeService, CategoryAttributeMapper categoryAttributeMapper) {
        this.service = service;
        this.mapper = mapper;
        this.categoryAttributeService = categoryAttributeService;
        this.categoryAttributeMapper = categoryAttributeMapper;
    }

    // ---------- READS -----------

    @GetMapping("/{id}")
    @Operation(summary = "Get category by id")
    public ResponseEntity<CategoryAdminResponseDTO> getById(@PathVariable Long id) {
        Category category = service.getByIdIncludingDeleted(id);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(category));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get category by id")
    public ResponseEntity<CategoryAdminResponseDTO> getBySlug(@PathVariable String slug) {
        Category category = service.getBySlugIncludingDeleted(slug);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(category));
    }

    @GetMapping
    @Operation(summary = "Get categories (paged)", description = "Search/sort/paginate categories using query params.")
    public ResponseEntity<Page<CategoryAdminResponseDTO>> getAll(
            @Valid @ModelAttribute CategoryAdminFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        SearchRequest<CategoryAdminFilter> req = SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        Page<CategoryAdminResponseDTO> page = service.searchAdmin(req).map(mapper::toAdminResponseDTO);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/search")
    @Operation(summary = "Search categories (POST)", description = "Same as GET but accepts a JSON body for complex filters.")
    public ResponseEntity<Page<CategoryAdminResponseDTO>> search(
            @Valid @RequestBody SearchRequest<CategoryAdminFilter> request,
            @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        SearchRequest<CategoryAdminFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        Page<CategoryAdminResponseDTO> page = service.searchAdmin(merged).map(mapper::toAdminResponseDTO);
        return ResponseEntity.ok(page);
    }

    // ---------- WRITES ----------

    @PostMapping
    public ResponseEntity<CategoryAdminResponseDTO> create(@Valid @RequestBody CategoryRequestDTO request) throws BadRequestException {
        Category category = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toAdminResponseDTO(category));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryAdminResponseDTO> update(@PathVariable Long id, @Valid @RequestBody CategoryUpdateDTO request) throws BadRequestException {
        Category category = service.update(id, request);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(category));
    }

    @PatchMapping("/{id}/root")
    public ResponseEntity<CategoryAdminResponseDTO> setAsRootCategory(@PathVariable Long id) {
        Category category = service.makeRoot(id);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<CategoryAdminResponseDTO> restore(@PathVariable Long id) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toAdminResponseDTO(service.restore(id)));
    }


//---------------Category Attributes-------------------------//

    @GetMapping("/{categoryId}/attributes/{attributeId}")
    @Operation(summary = "Admin: Get category-attributes by category id")
    public ResponseEntity<CategoryAttributeAdminResponseDTO> getCategoryAttributeLink(
            @PathVariable Long categoryId,
            @PathVariable Long attributeId) {
        CategoryAttribute attribute = categoryAttributeService.getByCategoryIdAndAttributeIdIncludingDeleted(categoryId, attributeId);
        CategoryAttributeAdminResponseDTO mappedAttribute = categoryAttributeMapper.toAdminResponseDTO(attribute);
        return ResponseEntity.ok(mappedAttribute);
    }

    @GetMapping("/{categoryId}/attributes/{attributeId}/usage")
    @Operation(summary = "Admin: Get category-attribute usage by category id and attribute id")
    public ResponseEntity<CategoryAttributeUsageDTO> summarizeUsage(
            @PathVariable Long categoryId,
            @PathVariable Long attributeId) {
        CategoryAttributeUsageDTO usage = categoryAttributeService.summarizeUsage(categoryId, attributeId);
        return ResponseEntity.ok(usage);
    }

    @GetMapping("/{categoryId}/attributes")
    @Operation(summary = "Admin: Get category-attributes by category id")
    public ResponseEntity<List<CategoryAttributeAdminResponseDTO>> getAllCategoryAttributeLinks(@PathVariable Long categoryId) {
        service.getByIdIncludingDeleted(categoryId); // Ensure category exists
        return ResponseEntity.ok(
                categoryAttributeService.getAllByCategoryIdIncludingDeleted(categoryId)
                        .stream()
                        .map(categoryAttributeMapper::toAdminResponseDTO)
                        .toList());
    }

    @PostMapping("/{categoryId}/attributes")
    @Operation(summary = "Add attribute to a category")
    public ResponseEntity<CategoryAttributeAdminResponseDTO> addCategoryAttributeLink(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryAttributeRequestDTO body
    ) {
        CategoryAttribute createdLink = categoryAttributeService.add(categoryId, body);
        return ResponseEntity.ok(categoryAttributeMapper.toAdminResponseDTO(createdLink));
    }

    @PutMapping("/{categoryId}/attributes")
    @Operation(summary = "Replace all category attributes")
    public ResponseEntity<List<CategoryAttributeAdminResponseDTO>> replaceCategoryAttributeLinks(
            @PathVariable Long categoryId,
            @Valid @RequestBody List<CategoryAttributeRequestDTO> body
    ) {
        return ResponseEntity.ok(
                categoryAttributeService.replaceAll(categoryId, body)
                        .stream()
                        .map(categoryAttributeMapper::toAdminResponseDTO)
                        .toList());
    }

    @PatchMapping("/{categoryId}/attributes/{attributeId}")
    @Operation(summary = "Update a category-attribute link")
    public ResponseEntity<CategoryAttributeAdminResponseDTO> updateCategoryAttributeLink(
            @PathVariable Long categoryId,
            @PathVariable Long attributeId,
            @Valid @RequestBody CategoryAttributeUpdateDTO body
    ) {
        CategoryAttribute updatedLink = categoryAttributeService.update(categoryId, attributeId, body);
        return ResponseEntity.ok(
                categoryAttributeMapper.toAdminResponseDTO(updatedLink));
    }

    @DeleteMapping("/{categoryId}/attributes/{attributeId}")
    @Operation(summary = "Remove an attribute from a category")
    public ResponseEntity<Void> deleteCategoryAttributeLink(
            @PathVariable Long categoryId,
            @PathVariable Long attributeId
    ) {
        categoryAttributeService.delete(categoryId, attributeId);
        return ResponseEntity.noContent().build();
    }
}