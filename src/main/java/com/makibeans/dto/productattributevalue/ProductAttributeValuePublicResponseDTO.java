package com.makibeans.dto.productattributevalue;

/**
 * Lean public DTO for ProductAttributeValue mapping.
 */
public record ProductAttributeValuePublicResponseDTO(
        Long id,
        Long productAttributeId,
        Long attributeValueId,
        String value
) {}

