package com.makibeans.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeTemplateRequestDTO {

    @NotBlank(message = "Name of attribute template cannot be blank.")
    @Size(min = 3, max = 50, message = "Name of attribute template must be between 3 and 50 characters.")
    private String name;
}





