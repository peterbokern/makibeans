package com.makibeans.dto;

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
    private String value;
}
