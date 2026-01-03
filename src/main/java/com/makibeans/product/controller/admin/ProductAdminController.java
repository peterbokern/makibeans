package com.makibeans.product.controller.admin;

import com.makibeans.product.dto.ProductAdminResponseDTO;
import com.makibeans.product.dto.ProductRequestDTO;
import com.makibeans.product.dto.ProductUpdateDTO;
import com.makibeans.product.filter.ProductAdminFilter;
import com.makibeans.product.mapper.ProductMapper;
import com.makibeans.product.model.Product;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.product.service.ProductService;
import com.makibeans.web.exceptions.ImageProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final ProductMapper productMapper;
    private final Logger logger = LoggerFactory.getLogger(ProductAdminController.class);

    public ProductAdminController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @Operation(summary = "Admin: Get product by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductAdminResponseDTO> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        return ResponseEntity.ok(productMapper.toAdminResponseDTO(product));
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
        Page<Product> page = productService.searchAdmin(req);
        Page<ProductAdminResponseDTO> result = page.map(productMapper::toAdminResponseDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/search")
    @Operation(
            summary = "Admin: Search products (POST)",
            description = "Same as GET but accepts a JSON body for complex filters."
    )
    public ResponseEntity<Page<ProductAdminResponseDTO>> search(
            @Valid @RequestBody SearchRequest<ProductAdminFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductAdminFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        Page<Product> page = productService.searchAdmin(merged);
        Page<ProductAdminResponseDTO> result = page.map(productMapper::toAdminResponseDTO);
        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------------------------
    // CREATE / UPDATE
    // -------------------------------------------------------------------------

    @Operation(summary = "Admin: Create a new product")
    @PostMapping
    public ResponseEntity<ProductAdminResponseDTO> create(@Valid @RequestBody ProductRequestDTO requestDTO) {
        Product created = productService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(productMapper.toAdminResponseDTO(created));
    }

    @Operation(summary = "Admin: Update a product by ID")
    @PutMapping("/{id}")
    public ResponseEntity<ProductAdminResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO updateDTO
    ) {
        Product updated = productService.update(id, updateDTO);
        return ResponseEntity.ok(productMapper.toAdminResponseDTO(updated));
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

        Product product = productService.uploadProductImage(productId, image);
        ProductAdminResponseDTO dto = productMapper.toAdminResponseDTO(product);

        logger.info("Uploaded image for product (id: {}, name: {}, filename: {}, type: {})",
                productId, dto.name(), originalFilename, fileType);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Upload-Message", "Product image uploaded successfully for '" + dto.name() + "'");
        headers.add("X-Original-Filename", originalFilename);
        headers.add("X-File-Type", fileType);

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(dto);
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
