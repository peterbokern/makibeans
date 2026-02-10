package com.makibeans.productattribute.dto;

import com.makibeans.productattributevalue.dto.ProductAttributeValuePublicResponseDTO;

import java.util.List;

/**
 * Lean public DTO for a product-attribute relationship used in product detail.
 */
public record ProductAttributePublicResponseDTO(
        Long id,
        Long categoryAttributeId,
        Long attributeId,
        String attributeName,
        String attributeSlug,
        String dataType,
        String inputType,
        List<ProductAttributeValuePublicResponseDTO> values,
        Boolean visible
) {}

