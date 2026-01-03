package com.makibeans.size.controller.admin;

import com.makibeans.size.dto.SizeAdminResponseDTO;
import com.makibeans.size.dto.SizeRequestDTO;
import com.makibeans.size.dto.SizeUpdateDTO;
import com.makibeans.size.filter.SizeAdminFilter;
import com.makibeans.size.mapper.SizeMapper;
import com.makibeans.size.model.Size;
import com.makibeans.search.SearchRequest;
import com.makibeans.size.filter.SizeFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.size.service.SizeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/sizes")
@RequiredArgsConstructor
@Tag(name = "Sizes (Admin)", description = "Admin operations for managing sizes")
@PreAuthorize("hasRole('ADMIN')")
public class SizeAdminController {

    private final SizeService service;
    private final SizeMapper mapper;

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------
    @GetMapping("/{id}")
    @Operation(summary = "Admin: Get size by id")
    public ResponseEntity<SizeAdminResponseDTO> getById(@PathVariable Long id) {
        Size size = service.getById(id);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(size));
    }

    @GetMapping
    @Operation(
            summary = "Admin: Get sizes (paged)",
            description = "Search/sort/paginate sizes using query parameters."
    )
    public ResponseEntity<Page<SizeAdminResponseDTO>> getAll(
            @Valid @ModelAttribute SizeAdminFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<SizeAdminFilter> req =
                SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);

        Page<Size> page = service.searchAdmin(req);
        Page<SizeAdminResponseDTO> result = page.map(mapper::toAdminResponseDTO);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/search")
    @Operation(
            summary = "Admin: Search sizes (POST)",
            description = "Same as GET but accepts a JSON body for complex filters."
    )
    public ResponseEntity<Page<SizeAdminResponseDTO>> search(
            @Valid @RequestBody SearchRequest<SizeAdminFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<SizeAdminFilter> merged =
                SearchRequestUtils.mergeWithPageable(request, pageable);

        Page<Size> page = service.searchAdmin(merged);
        Page<SizeAdminResponseDTO> result = page.map(mapper::toAdminResponseDTO);

        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------------------------
    // CREATE / UPDATE
    // -------------------------------------------------------------------------
    @PostMapping
    @Operation(summary = "Admin: Create size")
    public ResponseEntity<SizeAdminResponseDTO> create(
            @Valid @RequestBody SizeRequestDTO body
    ) {
        Size created = service.create(body);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toAdminResponseDTO(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Admin: Update size")
    public ResponseEntity<SizeAdminResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody SizeUpdateDTO body
    ) {
        Size updated = service.update(id, body);
        return ResponseEntity.ok(mapper.toAdminResponseDTO(updated));
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------
    @DeleteMapping("/{id}")
    @Operation(summary = "Admin: Delete size")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
