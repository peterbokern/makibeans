// language: java
package com.makibeans.controller.admin;

import com.makibeans.dto.attribute.AttributeAdminResponseDTO;
import com.makibeans.dto.attribute.AttributeRequestDTO;
import com.makibeans.dto.attribute.AttributeUpdateDTO;
import com.makibeans.mapper.AttributeMapper;
import com.makibeans.model.Attribute;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin controller for Attributes. Returns admin DTOs (with audit info).
 */
@RestController
@RequestMapping("/admin/attributes")
@RequiredArgsConstructor
@Tag(name = "Attributes (Admin)", description = "Admin operations for product attributes")
@PreAuthorize("hasRole('ADMIN')")
public class AttributeAdminController {

    private final AttributeService service;
    private final AttributeMapper mapper;

    // ---------- READS ----------

    @GetMapping("/{id}")
    @Operation(summary = "Admin: Get attribute by id (with audit)")
    public ResponseEntity<AttributeAdminResponseDTO> getById(@PathVariable Long id) {
        Attribute entity = service.getById(id);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(entity));
    }

    @GetMapping
    @Operation(summary = "Admin: Get attributes (paged)", description = "Search/sort/paginate attributes using query params.")
    public ResponseEntity<Page<AttributeAdminResponseDTO>> getAll(
            @Valid @ModelAttribute AttributeFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributeFilter> req = SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        Page<Attribute> page = service.search(req);
        return ResponseEntity.ok(page.map(mapper::toAdminResponseDTO));
    }

    @PostMapping("/search")
    @Operation(summary = "Admin: Search attributes (POST)", description = "Same as GET but accepts a JSON body for complex filters.")
    public ResponseEntity<Page<AttributeAdminResponseDTO>> search(
            @Valid @RequestBody SearchRequest<AttributeFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributeFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        Page<Attribute> page = service.search(merged);
        return ResponseEntity.ok(page.map(mapper::toAdminResponseDTO));
    }

    // ---------- WRITES (ADMIN) ----------

    @PostMapping
    @Operation(summary = "Admin: Create attribute", description = "Admin only.")
    public ResponseEntity<AttributeAdminResponseDTO> create(@Valid @RequestBody AttributeRequestDTO body) {
        Attribute created = service.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toAdminResponseDTO(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Admin: Update attribute", description = "Admin only. Full update semantics.")
    public ResponseEntity<AttributeAdminResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody AttributeUpdateDTO body
    ) {
        Attribute updated = service.update(id, body);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(updated));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Admin: Partially update attribute", description = "Admin only. Partial update semantics.")
    public ResponseEntity<AttributeAdminResponseDTO> patch(
            @PathVariable Long id,
            @Valid @RequestBody AttributeUpdateDTO body
    ) {
        Attribute updated = service.update(id, body);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Admin: Delete attribute", description = "Admin only. Soft/hard delete per service implementation.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}