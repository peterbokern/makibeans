package com.makibeans.controller;

import com.makibeans.dto.ProductPageDTO;
import com.makibeans.dto.ProductRequestDTO;
import com.makibeans.dto.ProductResponseDTO;
import com.makibeans.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing Products.
 * Provides endpoints for retrieving, creating, updating, and deleting products.
 */

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id the ID of the product to retrieve
     * @return a ResponseEntity containing the ProductResponseDTO
     */

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        ProductResponseDTO responseDTO = productService.getProductById(id);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Retrieves all products.
     *
     * @return a ResponseEntity containing a list of ProductResponseDTOs
     */

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> responseDTOS = productService.getAllProducts();
        return ResponseEntity.ok(responseDTOS);
    }

    /**
     * Filters products based on various criteria provided in the filters map.
     * The filters can include category ID, category name, price range, size, SKU, stock, and custom attributes.
     *
     * @param filters a map containing the filter criteria as key-value pairs
     * @return a ResponseEntity containing a list of ProductResponseDTOs representing the filtered products
     */

    @GetMapping("/filter")
    public ResponseEntity<ProductPageDTO> filterProducts(@RequestParam Map<String,String> filters) {
        ProductPageDTO content = productService.filterProducts(filters);
        return ResponseEntity.ok(content);
    }

    /**
     * Creates a new product.
     *
     * @param requestDTO the ProductRequestDTO containing product details
     * @return a ResponseEntity containing the created ProductResponseDTO
     */

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO responseDTO = productService.createProduct(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Updates a product by its ID.
     *
     * @param id         the ID of the product to update
     * @param requestDTO the ProductRequestDTO containing updated product details
     * @return a ResponseEntity containing the updated ProductResponseDTO
     */

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO responseDTO = productService.updateProduct(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id the ID of the product to delete
     * @return a ResponseEntity indicating the result of the operation
     */

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}
