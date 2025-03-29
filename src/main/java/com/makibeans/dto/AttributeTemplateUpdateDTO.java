package com.makibeans.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating an AttributeTemplate.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeTemplateUpdateDTO {

    @Size(min = 3, max = 50, message = "Attribute template name must be between 3 and 50 characters.")
    private String name;
}
