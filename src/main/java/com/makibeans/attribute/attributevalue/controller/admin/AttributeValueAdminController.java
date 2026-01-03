package com.makibeans.attribute.attributevalue.controller.admin;

import com.makibeans.attribute.attributevalue.dto.AttributeValueAdminResponseDTO;
import com.makibeans.attribute.attributevalue.dto.AttributeValueRequestDTO;
import com.makibeans.attribute.attributevalue.dto.AttributeValueUpdateDTO;
import com.makibeans.attribute.attributevalue.dto.AttributeValueUsageDTO;
import com.makibeans.attribute.attributevalue.filter.AttributeValueAdminFilter;
import com.makibeans.attribute.attributevalue.mapper.AttributeValueMapper;
import com.makibeans.attribute.attributevalue.model.AttributeValue;
import com.makibeans.search.SearchRequest;
import com.makibeans.attribute.attributevalue.filter.AttributeValueFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.attribute.attributevalue.service.AttributeValueService;
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
            @Valid @ModelAttribute AttributeValueAdminFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributeValueAdminFilter> req = SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        Page<AttributeValue> page = service.searchAdmin(req);
        return ResponseEntity.ok(page.map(mapper::toAdminResponseDTO));
    }

    @PostMapping("/search")
    @Operation(summary = "Admin: Search attribute values (POST)", description = "Same as GET but accepts a JSON body for complex filters.")
    public ResponseEntity<Page<AttributeValueAdminResponseDTO>> search(
            @Valid @RequestBody SearchRequest<AttributeValueAdminFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<AttributeValueAdminFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        Page<AttributeValue> page = service.searchAdmin(merged);
        return ResponseEntity.ok(page.map(mapper::toAdminResponseDTO));
    }

    @GetMapping("/{attributeValueId}/usage")
    @Operation(summary = "Get usage summary for an attribute value")
    public ResponseEntity<AttributeValueUsageDTO> summarizeUsage (@PathVariable Long attributeValueId) {
        AttributeValueUsageDTO usageDTO = service.summarizeAttributeValueUsage(attributeValueId);
        return ResponseEntity.ok(usageDTO);
    }

    // ---------- WRITES ----------

    @PostMapping
    @Operation(summary = "Admin: Create attribute value")
    public ResponseEntity<AttributeValueAdminResponseDTO> create(@Valid @RequestBody AttributeValueRequestDTO body) throws BadRequestException {
        AttributeValue created = service.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toAdminResponseDTO(created));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Admin: Partially update attribute value")
    public ResponseEntity<AttributeValueAdminResponseDTO> patch(@PathVariable Long id, @Valid @RequestBody AttributeValueUpdateDTO body) throws BadRequestException {
        AttributeValue updated = service.update(id, body);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Admin: Delete attribute value")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "Admin: Enable attribute value")
    public ResponseEntity<AttributeValueAdminResponseDTO> enable(@PathVariable Long id) throws BadRequestException {
        AttributeValue enabled = service.restore(id);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(enabled));
    }
}
