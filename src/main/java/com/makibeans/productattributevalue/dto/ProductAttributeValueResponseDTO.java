package com.makibeans.productattributevalue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO representing a ProductAttributeValue link with helpful denormalized fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeValueResponseDTO {

    private Long id;

    // Owner side (ProductAttribute)
    private Long productAttributeId;

    // Product context (denormalized for convenience)
    private Long productId;
    private String productName;

    // Attribute (template) context
    private Long attributeId;
    private String attributeName;

    // Value side
    private Long attributeValueId;
    private String value;

    // Audit
    private Instant createdAt;
    private Instant updatedAt;
}
