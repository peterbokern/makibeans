package com.makibeans.attribute.productattributevalue.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating/updating a ProductAttributeValue link.
 * Minimal fields to establish the association.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeValueRequestDTO {

    @NotNull(message = "productAttributeId is required")
    private Long productAttributeId;

    @NotNull(message = "attributeValueId is required")
    private Long attributeValueId;
}
