package com.makibeans.controller;

import com.makibeans.dto.productattribute.ProductAttributeRequestDTO;
import com.makibeans.dto.productattribute.ProductAttributeResponseDTO;
import com.makibeans.service.ProductAttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Product Attributes.
 * Provides endpoints for retrieving, creating, updating, and deleting product attribute values.
 */
@RestController
@RequestMapping("/product-attributes")
@Tag(name = "Product Attributes", description = "Operations for managing product attributes and their values")
public class ProductAttributeController {

    private final ProductAttributeService productAttributeService;

    public ProductAttributeController(ProductAttributeService productAttributeService) {
        this.productAttributeService = productAttributeService;
    }

    /**
     * Retrieves a product attribute by its ID.
     *
     * @param id the ID of the product attribute to retrieve
     * @return a ResponseEntity containing the ProductAttributeResponseDTO
     */
    @Operation(summary = "Get product attribute by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductAttributeResponseDTO> productAttributeResponseDTO(@PathVariable Long id) {
        ProductAttributeResponseDTO responseDTO = productAttributeService.getProductAttributeById(id);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Retrieves all product attributes.
     *
     * @return a ResponseEntity containing a list of ProductAttributeResponseDTOs
     */
    @Operation(summary = "Get all product attributes")
    @GetMapping()
    public ResponseEntity<List<ProductAttributeResponseDTO>> getAllProductAttributes() {
        List<ProductAttributeResponseDTO> productAttributeResponseDTOS = productAttributeService.getAllProductAttributes();
        return ResponseEntity.ok(productAttributeResponseDTOS);
    }

    /**
     * Creates a new product attribute.
     *
     * @param requestDTO the product attribute to create
     * @return a ResponseEntity containing the created ProductAttributeResponseDTO
     */
    @Operation(summary = "Create a new product attribute")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductAttributeResponseDTO> createProductAttribute(@Valid @RequestBody ProductAttributeRequestDTO requestDTO) {
        ProductAttributeResponseDTO responseDTO = productAttributeService.createProductAttribute(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Deletes a product attribute by its ID.
     *
     * @param id the ID of the product attribute to delete
     * @return a ResponseEntity indicating the result of the operation
     */
    @Operation(summary = "Delete a product attribute by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductAttribute(@PathVariable Long id) {
        productAttributeService.deleteProductAttribute(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Adds an AttributeValue to a ProductAttribute.
     *
     * @param productAttributeId the ID of the ProductAttribute.
     * @param attributeValueId   the ID of the AttributeValue to add.
     * @return a ResponseEntity indicating the result of the operation.
     */
    @Operation(summary = "Add an AttributeValue to a ProductAttribute")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{productAttributeId}/attribute-values/{attributeValueId}")
    public ResponseEntity<Void> addAttributeValue(@PathVariable Long productAttributeId, @PathVariable Long attributeValueId) {
        productAttributeService.addAttributeValue(productAttributeId, attributeValueId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Removes an AttributeValue from a ProductAttribute.
     *
     * @param productAttributeId the ID of the ProductAttribute.
     * @param attributeValueId   the ID of the AttributeValue to remove.
     * @return a ResponseEntity indicating the result of the operation.
     */
    @Operation(summary = "Remove an AttributeValue from a ProductAttribute")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productAttributeId}/attribute-values/{attributeValueId}")
    public ResponseEntity<Void> removeAttributeValue(@PathVariable Long productAttributeId, @PathVariable Long attributeValueId) {
        productAttributeService.removeAttributeValue(productAttributeId, attributeValueId);
        return ResponseEntity.noContent().build();
    }
}
