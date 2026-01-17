package com.makibeans.productattributevalue.dto;

/**
 * Lean public DTO for ProductAttributeValue mapping.
 */
/**
 * Public view for ProductAttributeValue (no audit info).
 */
public record ProductAttributeValuePublicResponseDTO(
        Long productAttributeValueId,
        Long attributeValueId,
        String value,
        String valueSlug
) {}
