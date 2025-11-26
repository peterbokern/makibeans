package com.makibeans.attribute.attributevalue.dto;

/**
 * Lean public DTO for AttributeValue used in attribute listings and product detail.
 */
public record AttributeValuePublicResponseDTO(
        Long id,
        Long attributeId,
        String attributeName,
        String value,
        String dataType
) {}

