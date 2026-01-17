package com.makibeans.attribute.dto;

import com.makibeans.attribute.model.AttributeDataType;
import com.makibeans.attribute.model.AttributeInputType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating or updating an attribute template.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeRequestDTO {

    @NotBlank(message = "Name of attribute template cannot be blank.")
    @Size(min = 3, max = 50, message = "Name of attribute template must be between 3 and 50 characters.")
    private String name;
    //TODO: add boolean includeDeleted

    @Size(max = 255, message = "Description of attribute template must be at most 255 characters.")
    private String description;

    @NotNull(message = "Data type of attribute template cannot be null.")
    private AttributeDataType dataType;

    @NotNull(message = "Input type of attribute  cannot be null.")
    private AttributeInputType inputType;

}





