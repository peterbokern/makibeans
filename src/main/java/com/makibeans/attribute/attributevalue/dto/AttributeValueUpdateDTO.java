package com.makibeans.attribute.attributevalue.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating an AttributeValue.
 */

@Data
@NoArgsConstructor
public class AttributeValueUpdateDTO {

    @Size(min = 1, max = 255, message = "Attribute value must be between 1 and 255 characters.")
    @NotNull(message = "Attribute value cannot be null.")
    private String rawValue;
}
