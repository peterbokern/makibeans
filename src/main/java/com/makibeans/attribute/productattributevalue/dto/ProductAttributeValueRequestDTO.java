package com.makibeans.attribute.productattributevalue.dto;

import com.makibeans.attribute.productattributevalue.validation.ValidAttributeValueChoice;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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