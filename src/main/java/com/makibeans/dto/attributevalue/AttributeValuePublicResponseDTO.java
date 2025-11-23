package com.makibeans.dto.attributevalue;

/**
 * Lean public DTO for AttributeValue used in attribute listings and product detail.
 */
public record AttributeValuePublicResponseDTO(
        Long id,
        Long attributeId,
        String attributeName,
        String value
) {}

