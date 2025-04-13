package com.makibeans.controller;

import com.makibeans.dto.product.ProductPageDTO;
import com.makibeans.dto.product.ProductRequestDTO;
import com.makibeans.dto.product.ProductResponseDTO;
import com.makibeans.dto.product.ProductUpdateDTO;
import com.makibeans.exceptions.ImageProcessingException;
import com.makibeans.service.ProductService;
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

import java.util.Map;

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
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        ProductResponseDTO responseDTO = productService.getProductById(id);
        logger.info("HALO Retrieved product with ID: {}", id);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Filters products based on various criteria provided in the filters map.
     * The filters can include category ID, category name, price range, size, SKU, stock, and custom attributes.
     *
     * @param filters a map containing the filter criteria as key-value pairs
     * @return a ResponseEntity containing a list of ProductResponseDTOs representing the filtered products
     */
    @Operation(summary = "Get all or search products with filters",
            description = "Fetch products with optional filtering, sorting, and pagination. " +
                    "Parameters include:\n" +
                    "- `search`: Partial match on product name, description, attribute values, or attribute template names.\n" +
                    "- `categoryId`: Exact match on category ID. Accepts multiple values separated by commas (e.g., `categoryId=1,2,3`).\n" +
                    "- `categoryName`: Exact match on category name. Accepts multiple values separated by commas (e.g., `categoryName=beans,grinders`).\n" +
                    "- `sizeId`: Exact match on size ID. Accepts multiple values separated by commas (e.g., `sizeId=10,20,30`).\n" +
                    "- `sizeName`: Exact match on size name. Accepts multiple values separated by commas (e.g., `sizeName=small,medium,large`).\n" +
                    "- `sku`: Exact match on SKU. Accepts multiple values separated by commas (e.g., `sku=abc123,xyz456`).\n" +
                    "- 'price': Sort field to allow sorting on price using min or max variant price for asc and desc respectively .\n" +
                    "- `minPrice`: Minimum price in cents.\n" +
                    "- `maxPrice`: Maximum price in cents.\n" +
                    "- `stock`: Filter by stock greater than or equal to this value.\n" +
                    "- `sort`: Field to sort by (`categoryName`, `priceInCents`, `productName`, `sizeName`).\n" +
                    "- `order`: Sort order (`asc`, `desc`).\n" +
                    "- `page`: Page number for pagination.\n" +
                    "- `size`: Number of items per page.")
    @GetMapping("")
    public ResponseEntity<ProductPageDTO> getProducts(@RequestParam Map<String, String> filters) {
        ProductPageDTO content = productService.findBySearchQuery(filters);
        return ResponseEntity.ok(content);
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
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO responseDTO = productService.createProduct(requestDTO);
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
        ProductResponseDTO responseDTO = productService.updateProduct(id, updateDTO);
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
        productService.deleteProduct(id);
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
