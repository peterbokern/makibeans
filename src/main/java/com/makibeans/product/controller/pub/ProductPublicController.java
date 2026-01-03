package com.makibeans.product.controller.pub;

import com.makibeans.product.dto.ProductPublicResponseDTO;
import com.makibeans.product.filter.ProductPublicFilter;
import com.makibeans.product.mapper.ProductMapper;
import com.makibeans.product.model.Product;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.makibeans.common.util.FileTypeUtils.detectImageContentType;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Public operations for browsing products and product images")
public class ProductPublicController {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final Logger logger = LoggerFactory.getLogger(ProductPublicController.class);

    public ProductPublicController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    // -------------------------------------------------------------------------
    // GET /products/{id}
    // -------------------------------------------------------------------------

    @Operation(summary = "Get product by ID (public)")
    @GetMapping("/{id}")
    public ResponseEntity<ProductPublicResponseDTO> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        return ResponseEntity.ok(productMapper.toPublicResponseDTO(product));
    }

    // -------------------------------------------------------------------------
    // GET /products  (paged search via query params)
    // -------------------------------------------------------------------------

    @Operation(
            summary = "Get products (paged, public)",
            description = "Search/sort/paginate products using query params."
    )
    @GetMapping
    public ResponseEntity<Page<ProductPublicResponseDTO>> getAll(
            @Valid @ModelAttribute ProductPublicFilter filters,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductPublicFilter> req = SearchRequestUtils.assemble(filters, search, false, pageable);
        Page<Product> page = productService.searchPublic(req);
        Page<ProductPublicResponseDTO> result = page.map(productMapper::toPublicResponseDTO);
        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------------------------
    // POST /products/search  (JSON body search)
    // -------------------------------------------------------------------------

    @PostMapping("/search")
    @Operation(
            summary = "Search products (POST, public)",
            description = "Same as GET but accepts a JSON body for complex filters."
    )
    public ResponseEntity<Page<ProductPublicResponseDTO>> search(
            @Valid @RequestBody SearchRequest<ProductPublicFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductPublicFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        merged.setIncludeDeleted(false); // Force exclude deleted for public API
        Page<Product> page = productService.searchPublic(merged);
        Page<ProductPublicResponseDTO> result = page.map(productMapper::toPublicResponseDTO);
        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------------------------
    // GET /products/{id}/image
    // -------------------------------------------------------------------------

    /**
     * Retrieves the image of a product by its ID.
     */
    @Operation(summary = "Get product image by ID (public)")
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long id) {
        byte[] image = productService.getProductImage(id);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, detectImageContentType(image))
                .body(image);
    }
}
