package com.makibeans.size.controller.pub;

import com.makibeans.size.dto.SizePublicResponseDTO;
import com.makibeans.size.filter.SizePublicFilter;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sizes")
@RequiredArgsConstructor
@Tag(name = "Sizes", description = "Public read API for sizes")
public class SizePublicController {

    private final SizeService service;
    private final SizeMapper mapper;

    // -------------------------------------------------------------------------
    // GET by ID
    // -------------------------------------------------------------------------
    @GetMapping("/{id}")
    @Operation(summary = "Get size by id (public)")
    public ResponseEntity<SizePublicResponseDTO> getById(@PathVariable Long id) {
        Size size = service.getById(id);
        return ResponseEntity.ok(mapper.toPublicResponseDTO(size));
    }

    // -------------------------------------------------------------------------
    // GET (paged + filters)
    // -------------------------------------------------------------------------
    @GetMapping
    @Operation(
            summary = "Get sizes (paged, public)",
            description = "Search/sort/paginate sizes using query parameters."
    )
    public ResponseEntity<Page<SizePublicResponseDTO>> getAll(
            @Valid @ModelAttribute SizePublicFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<SizePublicFilter> req =
                SearchRequestUtils.assemble(filters, search, false, pageable);

        Page<Size> page = service.searchPublic(req);
        Page<SizePublicResponseDTO> result = page.map(mapper::toPublicResponseDTO);

        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------------------------
    // POST /search
    // -------------------------------------------------------------------------
    @PostMapping("/search")
    @Operation(
            summary = "Search sizes (POST, public)",
            description = "Same as GET but accepts a JSON body for complex filters."
    )
    public ResponseEntity<Page<SizePublicResponseDTO>> search(
            @Valid @RequestBody SearchRequest<SizePublicFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<SizePublicFilter> merged =
                SearchRequestUtils.mergeWithPageable(request, pageable);

        Page<Size> page = service.searchPublic(merged);
        Page<SizePublicResponseDTO> result = page.map(mapper::toPublicResponseDTO);

        return ResponseEntity.ok(result);
    }
}
