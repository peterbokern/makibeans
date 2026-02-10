package com.makibeans.productattributevalue.dto;

import com.makibeans.productattributevalue.validation.ValidAttributeValueChoice;
import jakarta.validation.constraints.NotNull;


/**
 * Request DTO for creating/updating a ProductAttributeValue link.
 * Minimal fields to establish the association.
 */
@ValidAttributeValueChoice
public record ProductAttributeValueRequestDTO(
        @NotNull(message = "productAttributeId is required") Long productAttributeId,
        Long attributeValueId,
        String rawValue
) {}