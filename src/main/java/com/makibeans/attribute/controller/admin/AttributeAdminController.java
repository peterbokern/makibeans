// language: java
package com.makibeans.attribute.controller.admin;

import com.makibeans.attribute.dto.AttributeAdminResponseDTO;
import com.makibeans.attribute.dto.AttributeRequestDTO;
import com.makibeans.attribute.dto.AttributeUpdateDTO;
import com.makibeans.attribute.dto.AttributeUsageDTO;
import com.makibeans.attribute.filter.AttributeAdminFilter;
import com.makibeans.attribute.mapper.AttributeMapper;
import com.makibeans.attribute.model.Attribute;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.attribute.service.AttributeService;
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
        Attribute entity = service.getByIdIncludingDeleted(id);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(entity));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Admin: Get attribute by slug (with audit)")
    public ResponseEntity<AttributeAdminResponseDTO> getBySlug(@PathVariable String slug) {
        Attribute entity = service.getBySlugIncludingDeleted(slug);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(entity));
    }

    @GetMapping
    @Operation(summary = "Admin: Get attributes (paged)", description = "Search/sort/paginate attributes using query params.")
    public ResponseEntity<Page<AttributeAdminResponseDTO>> getAll(
            @Valid @ModelAttribute AttributeAdminFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributeAdminFilter> req = SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        Page<Attribute> page = service.searchAdmin(req);
        return ResponseEntity.ok(page.map(mapper::toAdminResponseDTO));
    }

    @PostMapping("/search")
    @Operation(summary = "Admin: Search attributes (POST)", description = "Same as GET but accepts a JSON body for complex filters.")
    public ResponseEntity<Page<AttributeAdminResponseDTO>> search(
            @Valid @RequestBody SearchRequest<AttributeAdminFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributeAdminFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        Page<Attribute> page = service.searchAdmin(merged);
        return ResponseEntity.ok(page.map(mapper::toAdminResponseDTO));
    }

    @GetMapping("/{attributeId}/usage")
    @Operation(summary = "Get usage summary for an attribute")
    public ResponseEntity<AttributeUsageDTO> summarizeUsage (@PathVariable Long attributeId) {
        AttributeUsageDTO usage = service.summarizeAttributeUsage(attributeId);
        return ResponseEntity.ok(usage);
    }

    // ---------- WRITES (ADMIN) ----------

    @PostMapping
    @Operation(summary = "Admin: Create attribute", description = "Admin only.")
    public ResponseEntity<AttributeAdminResponseDTO> create(@Valid @RequestBody AttributeRequestDTO body) {
        Attribute created = service.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toAdminResponseDTO(created));
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
    public ResponseEntity<Void> delete(@PathVariable Long id) throws BadRequestException {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "Admin: Restore deleted attribute", description = "Admin only. Restore a soft-deleted attribute.")
    public ResponseEntity<AttributeAdminResponseDTO> restore(@PathVariable Long id) throws BadRequestException {
        Attribute restored = service.restore(id);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(restored));
    }
}