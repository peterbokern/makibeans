// language: java
package com.makibeans.attribute.categoryattribute.controller.admin;

import com.makibeans.attribute.categoryattribute.dto.CategoryAttributeAdminResponseDTO;
import com.makibeans.attribute.categoryattribute.dto.CategoryAttributeRequestDTO;
import com.makibeans.attribute.categoryattribute.dto.CategoryAttributeUpdateDTO;
import com.makibeans.attribute.categoryattribute.dto.CategoryAttributeUsageDTO;
import com.makibeans.attribute.categoryattribute.filter.CategoryAttributeAdminFilter;
import com.makibeans.attribute.categoryattribute.filter.CategoryAttributeFilter;
import com.makibeans.attribute.categoryattribute.mapper.CategoryAttributeMapper;
import com.makibeans.attribute.categoryattribute.model.CategoryAttribute;
import com.makibeans.attribute.categoryattribute.service.CategoryAttributeService;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.size.dto.SizeUsageDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
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
            @Valid @ModelAttribute CategoryAttributeAdminFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<CategoryAttributeAdminFilter> req =
                SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);

        Page<CategoryAttribute> page = service.searchAdmin(req);
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
            @Valid @RequestBody SearchRequest<CategoryAttributeAdminFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<CategoryAttributeAdminFilter> merged =
                SearchRequestUtils.mergeWithPageable(request, pageable);

        Page<CategoryAttribute> page = service.searchAdmin(merged);
        Page<CategoryAttributeAdminResponseDTO> result =
                page.map(mapper::toAdminResponseDTO);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/usage")
    @Operation(summary = "Admin: Get usage summary for a category-attribute link")
    public CategoryAttributeUsageDTO summarizeUsage(@PathVariable Long id) {
        CategoryAttribute entity = service.getById(id);
        return service.summarizeCategoryAttributeUsage(id);
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

    @PostMapping("/{id}/restore")
    @Operation(
            summary = "Admin: Restore deleted category-attribute link",
            description = "Restore a soft-deleted category-attribute link."
    )
    public ResponseEntity<CategoryAttributeAdminResponseDTO> restore(@PathVariable Long id) throws BadRequestException {
        CategoryAttribute restored = service.restore(id);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(restored));
    }
}
