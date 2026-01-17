package com.makibeans.product.controller.pub;

import com.makibeans.product.dto.ProductPublicResponseDTO;
import com.makibeans.product.filter.ProductPublicFilter;
import com.makibeans.product.mapper.ProductMapper;
import com.makibeans.productvariant.dto.ProductVariantPublicResponseDTO;
import com.makibeans.productvariant.mapper.ProductVariantMapper;
import com.makibeans.productvariant.service.ProductVariantService;
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

import java.util.List;

import static com.makibeans.common.util.FileTypeUtils.detectImageContentType;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Public operations for browsing products and product images")
public class ProductPublicController {

    private final ProductService productService;
    private final ProductVariantService productVariantService;
    private final ProductVariantMapper productVariantMapper;
    private final Logger logger = LoggerFactory.getLogger(ProductPublicController.class);

    public ProductPublicController(ProductService productService, ProductMapper productMapper, ProductVariantService productVariantService, ProductVariantMapper productVariantMapper) {
        this.productService = productService;
        this.productVariantService = productVariantService;
        this.productVariantMapper = productVariantMapper;
    }

    // -------------------------------------------------------------------------
    // GET /products/{id}
    // -------------------------------------------------------------------------

    @Operation(summary = "Get product by ID (public)")
    @GetMapping("/{id}")
    public ResponseEntity<ProductPublicResponseDTO> getById(@PathVariable Long id) {
        return null ;
        //ResponseEntity.ok(productService.getById(id));
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
        return ResponseEntity.ok(productService.searchPublic(req));
    }

    @GetMapping("/{id}/variants")
    public ResponseEntity<List<ProductVariantPublicResponseDTO>> getVariantsByProductId(@PathVariable Long id) {
        var variants = productVariantService.getPublicVariantsByProductId(id).stream()
                .map(productVariantMapper::toPublicResponseDTO)
                .toList();
        return ResponseEntity.ok(variants);
    }

    // -------------------------------------------------------------------------
    // POST /products/search  (JSON body search)
    // -------------------------------------------------------------------------

    @PostMapping("/search")
    @Operation(
            summary = "Search products (POST, public)",
            description =
                    "Search products  using JSON body for complex filters." +
                    "Supports attribute-value filtering:" +
                    "If attributeFilters is present categoryId must be provided and must be 1 value" +
                            "attribute values must be provided in list e.g. 'origin' : ['chili', 'argentina']" +
                            "Attributes must be filterable, this is set on category-attribute links"
    )
    public ResponseEntity<Page<ProductPublicResponseDTO>> search(
            @Valid @RequestBody SearchRequest<ProductPublicFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductPublicFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        merged.setIncludeDeleted(false); // Force exclude deleted for public API
        return ResponseEntity.ok(productService.searchPublic(merged));
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
