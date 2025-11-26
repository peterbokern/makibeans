package com.makibeans.attribute.productattribute.dto;

/**
 * Lean public DTO for a product-attribute relationship used in product detail.
 */
public record ProductAttributePublicResponseDTO(
        Long id,
        Long productId,
        String productName,
        Long attributeId,
        String attributeName
) {}

