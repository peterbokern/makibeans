package com.makibeans.controller.admin;

import com.makibeans.dto.category.CategoryAdminResponseDTO;
import com.makibeans.dto.category.CategoryRequestDTO;
import com.makibeans.dto.category.CategoryUpdateDTO;
import com.makibeans.mapper.CategoryMapper;
import com.makibeans.model.Category;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.CategoryFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.service.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/categories")
@Tag(name = "Admin Categories", description = "Admin operations for managing categories")
public class CategoryAdminController {

    private final CategoryService service;
    private final CategoryMapper mapper;

    public CategoryAdminController(CategoryService service, CategoryMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    // ---------- READS -----------

    @GetMapping("/{id}")
    @Operation(summary = "Get category by id")
    public ResponseEntity<CategoryAdminResponseDTO> getById(@PathVariable Long id) {
        Category category = service.getById(id);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(category));
    }

    @GetMapping
    @Operation(summary = "Get categories (paged)", description = "Search/sort/paginate categories using query params.")
    public ResponseEntity<Page<CategoryAdminResponseDTO>> getAll(
            @Valid @ModelAttribute CategoryFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<CategoryFilter> req = SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        Page<CategoryAdminResponseDTO> page = service.search(req).map(mapper::toAdminResponseDTO);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/search")
    @Operation(summary = "Search categories (POST)", description = "Same as GET but accepts a JSON body for complex filters.")
    public ResponseEntity<Page<CategoryAdminResponseDTO>> search(
            @Valid @RequestBody SearchRequest<CategoryFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<CategoryFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        Page<CategoryAdminResponseDTO> page = service.search(merged).map(mapper::toAdminResponseDTO);
        return ResponseEntity.ok(page);
    }


    // ---------- WRITES ----------

    @PostMapping
    public ResponseEntity<CategoryAdminResponseDTO> create(@Valid @RequestBody CategoryRequestDTO request) {
        Category category = service.create(request);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryAdminResponseDTO> update(@PathVariable Long id, @Valid @RequestBody CategoryUpdateDTO request) {
        Category category = service.update(id, request);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}