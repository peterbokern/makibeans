package com.makibeans.attribute.attribute.dto;

import com.makibeans.attribute.attribute.model.AttributeInputType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating an Attribute.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeUpdateDTO {

    @Size(min = 3, max = 50, message = "Attribute template name must be between 3 and 50 characters.")
    private String name;

    @Size(max = 255, message = "Description of attribute template must be at most 255 characters.")
    private String description;

    private AttributeInputType inputType;

}
