package com.makibeans.attribute.productattribute.dto;

import com.makibeans.attribute.productattributevalue.dto.ProductAttributeValuePublicResponseDTO;

import java.util.List;

/**
 * Lean public DTO for a product-attribute relationship used in product detail.
 */
public record ProductAttributePublicResponseDTO(
        Long id,
        Long productId,
        String productName,
        Long categoryAttributeId,
        Long attributeId,
        String attributeName,
        List<ProductAttributeValuePublicResponseDTO> values,
        Boolean visible
) {}

