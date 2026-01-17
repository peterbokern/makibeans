package com.makibeans.product.controller.admin;

import com.makibeans.product.dto.ProductAdminResponseDTO;
import com.makibeans.product.dto.ProductRequestDTO;
import com.makibeans.product.dto.ProductUpdateDTO;
import com.makibeans.product.filter.ProductAdminFilter;

import com.makibeans.search.SearchRequest;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.product.service.ProductService;
import com.makibeans.web.exceptions.ImageProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/products")
@Tag(name = "Products (Admin)", description = "Admin operations for managing products and product images")
@PreAuthorize("hasRole('ADMIN')")
public class ProductAdminController {

    private final ProductService productService;

    public ProductAdminController(ProductService productService) {
        this.productService = productService;
    }

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @Operation(summary = "Admin: Get product by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductAdminResponseDTO> getById(@PathVariable Long id) {
        ProductAdminResponseDTO product = productService.getByIdIncludingDeleted(id);
        return ResponseEntity.ok(product);
    }

    @Operation(
            summary = "Admin: Get products (paged)",
            description = "Search/sort/paginate products using query params."
    )
    @GetMapping
    public ResponseEntity<Page<ProductAdminResponseDTO>> getAll(
            @Valid @ModelAttribute ProductAdminFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductAdminFilter> req = SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        return ResponseEntity.ok(productService.searchAdmin(req));
    }

    @PostMapping("/search")
    @Operation(
            summary = "Admin: Search products (POST)",
            description =  "Search products  using JSON body for complex filters." +
                    "Supports attribute-value filtering:" +
                    "If attributeFilters is present categoryId must be provided and must be 1 value" +
            "attribute values must be provided in list e.g. 'origin' : ['chili', 'argentina']" +
                    "Attributes must be filterable, this is set on category-attribute links"
    )
    public ResponseEntity<Page<ProductAdminResponseDTO>> search(
            @Valid @RequestBody SearchRequest<ProductAdminFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductAdminFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        return ResponseEntity.ok(productService.searchAdmin(merged));
    }

    // -------------------------------------------------------------------------
    // CREATE / UPDATE
    // -------------------------------------------------------------------------

    @Operation(summary = "Admin: Create a new product")
    @PostMapping
    public ResponseEntity<ProductAdminResponseDTO> create(@Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductAdminResponseDTO created = productService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Admin: Update a product by ID")
    @PutMapping("/{id}")
    public ResponseEntity<ProductAdminResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO updateDTO
    ) {
        return ResponseEntity.ok(productService.update(id, updateDTO));
    }

    // -------------------------------------------------------------------------
    // IMAGE UPLOAD / DELETE
    // -------------------------------------------------------------------------

    @Operation(summary = "Admin: Upload image for product")
    @PostMapping("/{id}/image")
    public ResponseEntity<ProductAdminResponseDTO> uploadProductImage(
            @PathVariable("id") Long productId,
            @RequestParam("image") MultipartFile image
    ) throws ImageProcessingException {

        String originalFilename = image.getOriginalFilename() != null ? image.getOriginalFilename() : "unknown";
        String fileType = image.getContentType() != null ? image.getContentType() : "application/octet-stream";

        ProductAdminResponseDTO product = productService.uploadProductImage(productId, image);


        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Upload-Message", "Product image uploaded successfully for '" + product.getName() + "'");
        headers.add("X-Original-Filename", originalFilename);
        headers.add("X-File-Type", fileType);

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(product);
    }

    @Operation(summary = "Admin: Delete a product by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Admin: Delete product image by ID")
    @DeleteMapping("/{id}/image")
    public ResponseEntity<Void> deleteProductImage(@PathVariable Long id) {
        productService.deleteProductImage(id);
        return ResponseEntity.noContent().build();
    }
}
