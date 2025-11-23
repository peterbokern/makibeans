package com.makibeans.attributevalue.controller.admin;

import com.makibeans.attributevalue.dto.AttributeValueAdminResponseDTO;
import com.makibeans.attributevalue.dto.AttributeValueRequestDTO;
import com.makibeans.attributevalue.dto.AttributeValueUpdateDTO;
import com.makibeans.attributevalue.mapper.AttributeValueMapper;
import com.makibeans.attributevalue.model.AttributeValue;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.AttributeValueFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.attributevalue.service.AttributeValueService;
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
 * Admin AttributeValue REST controller.
 * Handles admin operations for managing attribute values.
 */
@RestController
@RequestMapping("/admin/attribute-values")
@RequiredArgsConstructor
@Tag(name = "Attribute Values (Admin)", description = "Manage attribute values (admin)")
@PreAuthorize("hasRole('ADMIN')")
public class AttributeValueAdminController {

    private final AttributeValueService service;
    private final AttributeValueMapper mapper;

    // ---------- READS ----------

    @GetMapping("/{id}")
    @Operation(summary = "Admin: Get attribute value by id")
    public ResponseEntity<AttributeValueAdminResponseDTO> getById(@PathVariable Long id) {
        AttributeValue entity = service.getById(id);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(entity));
    }

    @GetMapping
    @Operation(summary = "Admin: Get attribute values (paged)", description = "Search/sort/paginate attribute values using query params.")
    public ResponseEntity<Page<AttributeValueAdminResponseDTO>> getAll(
            @Valid @ModelAttribute AttributeValueFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributeValueFilter> req = SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        Page<AttributeValue> page = service.search(req);
        return ResponseEntity.ok(page.map(mapper::toAdminResponseDTO));
    }

    @PostMapping("/search")
    @Operation(summary = "Admin: Search attribute values (POST)", description = "Same as GET but accepts a JSON body for complex filters.")
    public ResponseEntity<Page<AttributeValueAdminResponseDTO>> search(
            @Valid @RequestBody SearchRequest<AttributeValueFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributeValueFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        Page<AttributeValue> page = service.search(merged);
        return ResponseEntity.ok(page.map(mapper::toAdminResponseDTO));
    }

    // ---------- WRITES ----------

    @PostMapping
    @Operation(summary = "Admin: Create attribute value")
    public ResponseEntity<AttributeValueAdminResponseDTO> create(@Valid @RequestBody AttributeValueRequestDTO body) {
        AttributeValue created = service.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toAdminResponseDTO(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Admin: Update attribute value")
    public ResponseEntity<AttributeValueAdminResponseDTO> update(@PathVariable Long id, @Valid @RequestBody AttributeValueUpdateDTO body) {
        AttributeValue updated = service.update(id, body);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(updated));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Admin: Partially update attribute value")
    public ResponseEntity<AttributeValueAdminResponseDTO> patch(@PathVariable Long id, @Valid @RequestBody AttributeValueUpdateDTO body) {
        AttributeValue updated = service.update(id, body);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Admin: Delete attribute value")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
