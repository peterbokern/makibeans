package com.makibeans.dto.productattributevalue;

/**
 * Lean public DTO for ProductAttributeValue mapping.
 */
/**
 * Public view for ProductAttributeValue (no audit info).
 */
public record ProductAttributeValuePublicResponseDTO(
        Long id,

        // Owner side (ProductAttribute)
        Long productAttributeId,

        // Product context
        Long productId,
        String productName,

        // Attribute (template) context
        Long attributeId,
        String attributeName,

        // Value side
        Long attributeValueId,
        String value
) {}
