package com.makibeans.category.controller.admin;

import com.makibeans.category.dto.CategoryAdminResponseDTO;
import com.makibeans.category.dto.CategoryRequestDTO;
import com.makibeans.category.dto.CategoryUpdateDTO;
import com.makibeans.category.mapper.CategoryMapper;
import com.makibeans.category.model.Category;
import com.makibeans.search.SearchRequest;
import com.makibeans.category.filter.CategoryAdminFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(CategoryAdminController.class);

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
            @Valid @ModelAttribute CategoryAdminFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        logger.info("pageable sort: {}", pageable.getSort());
        SearchRequest<CategoryAdminFilter> req = SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        logger.info("Sort By: {}", req.getSortBy());
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
        return ResponseEntity.ok(mapper.toAdminResponseDTO(category));
    }

    @PutMapping("/{id}")
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
}