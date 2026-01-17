package com.makibeans.attributevalue.dto;

import jakarta.validation.constraints.Min;
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
    private String rawValue;

    @Min(value = 0, message = "Sort order must be zero or a positive integer.")
    private Integer sortOrder ;
}
