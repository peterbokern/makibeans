// language: java
package com.makibeans.categoryattribute.controller.admin;

import com.makibeans.categoryattribute.dto.CategoryAttributeAdminResponseDTO;
import com.makibeans.categoryattribute.dto.CategoryAttributeRequestDTO;
import com.makibeans.categoryattribute.dto.CategoryAttributeUpdateDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/category-attributes")
@RequiredArgsConstructor
@Tag(name = "Category Attributes (Admin)", description = "Admin operations for category-attribute associations")
@PreAuthorize("hasRole('ADMIN')")
public class CategoryAttributeAdminController {

    private final CategoryAttributeService service;
    private final CategoryAttributeMapper mapper;

    // ---------- READS (ADMIN) ----------

    @GetMapping("/{id}")
    @Operation(summary = "Admin: Get category-attribute by id")
    public ResponseEntity<CategoryAttributeAdminResponseDTO> getById(@PathVariable Long id) {
        CategoryAttribute entity = service.getById(id);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(entity));
    }

    @GetMapping
    @Operation(
            summary = "Admin: Get category-attributes (paged)",
            description = "Search/sort/paginate category-attributes using query params."
    )
    public ResponseEntity<Page<CategoryAttributeAdminResponseDTO>> getAll(
            @Valid @ModelAttribute CategoryAttributeFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<CategoryAttributeFilter> req =
                SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);

        Page<CategoryAttribute> page = service.search(req);
        Page<CategoryAttributeAdminResponseDTO> result =
                page.map(mapper::toAdminResponseDTO);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/search")
    @Operation(
            summary = "Admin: Search category-attributes (POST)",
            description = "Same as GET but accepts a JSON body for complex filters."
    )
    public ResponseEntity<Page<CategoryAttributeAdminResponseDTO>> search(
            @Valid @RequestBody SearchRequest<CategoryAttributeFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<CategoryAttributeFilter> merged =
                SearchRequestUtils.mergeWithPageable(request, pageable);

        Page<CategoryAttribute> page = service.search(merged);
        Page<CategoryAttributeAdminResponseDTO> result =
                page.map(mapper::toAdminResponseDTO);

        return ResponseEntity.ok(result);
    }

    // ---------- WRITES (ADMIN) ----------

    @PostMapping
    @Operation(summary = "Admin: Create category-attribute link")
    public ResponseEntity<CategoryAttributeAdminResponseDTO> create(
            @Valid @RequestBody CategoryAttributeRequestDTO body
    ) {
        CategoryAttribute created = service.create(body);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toAdminResponseDTO(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Admin: Update category-attribute link")
    public ResponseEntity<CategoryAttributeAdminResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryAttributeUpdateDTO body
    ) {
        CategoryAttribute updated = service.update(id, body);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(updated));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Admin: Partially update category-attribute link")
    public ResponseEntity<CategoryAttributeAdminResponseDTO> patch(
            @PathVariable Long id,
            @Valid @RequestBody CategoryAttributeUpdateDTO body
    ) {
        CategoryAttribute updated = service.update(id, body);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Admin: Delete category-attribute link",
            description = "Soft/hard delete per service implementation."
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
