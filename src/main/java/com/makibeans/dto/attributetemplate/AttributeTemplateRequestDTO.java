package com.makibeans.dto.attributetemplate;

import jakarta.validation.constraints.NotBlank;
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
public class AttributeTemplateRequestDTO {

    @NotBlank(message = "Name of attribute template cannot be blank.")
    @Size(min = 3, max = 50, message = "Name of attribute template must be between 3 and 50 characters.")
    private String name;
}





