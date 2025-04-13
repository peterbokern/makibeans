package com.makibeans.controller;

import com.makibeans.dto.productvariant.ProductVariantRequestDTO;
import com.makibeans.dto.productvariant.ProductVariantResponseDTO;
import com.makibeans.dto.productvariant.ProductVariantUpdateDTO;
import com.makibeans.service.ProductVariantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Product Variants.
 * Provides endpoints for retrieving, creating, updating, and deleting product variants.
 */
@RestController
@RequestMapping("/product-variants")
@Tag(name = "Product Variants", description = "CRUD operations for product variants")
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    public ProductVariantController(ProductVariantService productVariantService) {
        this.productVariantService = productVariantService;
    }

    /**
     * Retrieves a product variant by its ID.
     *
     * @param id the ID of the product variant to retrieve
     * @return a ResponseEntity containing the ProductVariantResponseDTO
     */
    @Operation(summary = "Get product variant by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductVariantResponseDTO> getProductVariantById(@PathVariable Long id) {
        ProductVariantResponseDTO responseDTO = productVariantService.getProductVariantById(id);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Retrieves all product variants.
     *
     * @return a ResponseEntity containing a list of ProductVariantResponseDTOs
     */
    @Operation(summary = "Get all product variants")
    @GetMapping
    public ResponseEntity<List<ProductVariantResponseDTO>> getAllProductVariants() {
        List<ProductVariantResponseDTO> responseDTOS = productVariantService.getAllProductVariants();
        return ResponseEntity.ok(responseDTOS);
    }

    /**
     * Creates a new product variant.
     *
     * @param requestDTO the ProductVariantRequestDTO containing product variant details
     * @return a ResponseEntity containing the created ProductVariantResponseDTO
     */
    @Operation(summary = "Create a new product variant")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductVariantResponseDTO> createProductVariant(@Valid @RequestBody ProductVariantRequestDTO requestDTO) {
        ProductVariantResponseDTO responseDTO = productVariantService.createProductVariant(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Updates a product variant by its ID.
     *
     * @param id         the ID of the product variant to update
     * @param requestDTO the ProductVariantUpdateDTO containing updated product variant details
     * @return a ResponseEntity containing the updated ProductVariantResponseDTO
     */
    @Operation(summary = "Update product variant by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductVariantResponseDTO> updateProductVariant(@PathVariable Long id, @Valid @RequestBody ProductVariantUpdateDTO requestDTO) {
        ProductVariantResponseDTO responseDTO = productVariantService.updateProductVariant(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Deletes a product variant by its ID.
     *
     * @param id the ID of the product variant to delete
     * @return a ResponseEntity indicating the result of the operation
     */
    @Operation(summary = "Delete product variant by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductVariant(@PathVariable Long id) {
        productVariantService.deleteProductVariant(id);
        return ResponseEntity.noContent().build();
    }
}
