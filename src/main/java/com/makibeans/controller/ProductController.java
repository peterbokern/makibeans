package com.makibeans.controller;

import com.makibeans.dto.product.ProductRequestDTO;
import com.makibeans.dto.product.ProductResponseDTO;
import com.makibeans.dto.product.ProductUpdateDTO;
import com.makibeans.exceptions.ImageProcessingException;
import com.makibeans.service.service.ProductService;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.ProductFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import static com.makibeans.util.FileTypeUtils.detectImageContentType;

/**
 * REST controller for managing Products.
 * Provides endpoints for retrieving, creating, updating, and deleting products.
 */
@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Operations for managing products and product images")
public class ProductController {

    private final ProductService productService;
    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id the ID of the product to retrieve
     * @return a ResponseEntity containing the ProductResponseDTO
     */
    @Operation(summary = "Get product by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        ProductResponseDTO responseDTO = productService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Get products (paged) - supports query param filtering, search and pagination.
     */
    @Operation(summary = "Get products (paged)", description = "Search/sort/paginate products using query params.")
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAll(
            @Valid @ModelAttribute ProductFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductFilter> req = SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        return ResponseEntity.ok(productService.search(req));
    }

    @PostMapping("/search")
    @Operation(summary = "Search products (POST)", description = "Same as GET but accepts a JSON body for complex filters.")
    public ResponseEntity<Page<ProductResponseDTO>> search(
            @Valid @RequestBody SearchRequest<ProductFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<ProductFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        return ResponseEntity.ok(productService.search(merged));
    }

    /**
     * Retrieves the image of a product by its ID.
     *
     * @param id the ID of the product whose image is to be retrieved.
     * @return a ResponseEntity containing the byte array representing the product image.
     */
    @Operation(summary = "Get product image by ID")
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long id) {
        byte[] image = productService.getProductImage(id);
        return ResponseEntity
                .ok()
                .header("Content-Type", detectImageContentType(image))
                .body(image);
    }

    /**
     * Creates a new product.
     *
     * @param requestDTO the ProductRequestDTO containing product details
     * @return a ResponseEntity containing the created ProductResponseDTO
     */
    @Operation(summary = "Create a new product")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO responseDTO = productService.create(requestDTO);
        return ResponseEntity.status(201).body(responseDTO);
    }

    /**
     * Uploads an image for a product (Admin only).
     *
     * @param productId the ID of the product to upload the image for.
     * @param image     the image file to upload.
     * @throws ImageProcessingException if the image file is empty or null or if an I/O error occurs during image processing.
     */
    @Operation(summary = "Upload image for product")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/image")
    public ResponseEntity<ProductResponseDTO> uploadProductImage(
            @PathVariable("id") Long productId,
            @RequestParam("image") MultipartFile image) {

        String originalFilename = image.getOriginalFilename() != null ? image.getOriginalFilename() : "unknown";
        String fileType = image.getContentType() != null ? image.getContentType() : "application/octet-stream";

        ProductResponseDTO productResponseDTO = productService.uploadProductImage(productId, image);

        logger.info("Uploaded image for product (id: {}, name: {}, filename: {}, type: {})", productId, productResponseDTO.getName(), originalFilename, fileType);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Upload-Message", "Category image uploaded successfully for '" + productResponseDTO.getName() + "'");
        headers.add("X-Original-Filename", originalFilename);
        headers.add("X-File-Type", fileType);

        return ResponseEntity.ok()
                .headers(headers)
                .body(productResponseDTO);
    }

    /**
     * Updates a product by its ID.
     *
     * @param id        the ID of the product to update
     * @param updateDTO the ProductUpdateDTO containing updated product details
     * @return a ResponseEntity containing the updated ProductResponseDTO
     */
    @Operation(summary = "Update a product by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO updateDTO) {
        ProductResponseDTO responseDTO = productService.update(id, updateDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id the ID of the product to delete
     * @return a ResponseEntity indicating the result of the operation
     */
    @Operation(summary = "Delete a product by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes the image of a product by its ID (Admin only).
     *
     * @param id the ID of the product whose image is to be deleted.
     * @return a ResponseEntity indicating the result of the operation.
     */
    @Operation(summary = "Delete product image by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/image")
    public ResponseEntity<Void> deleteProductImage(@PathVariable Long id) {
        productService.deleteProductImage(id);
        return ResponseEntity.noContent().build();
    }
}
