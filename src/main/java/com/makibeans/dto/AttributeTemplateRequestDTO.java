package com.makibeans.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AttributeTemplateRequestDTO {

    @NotBlank(message = "Name of attribute template cannot be blank.")
    private String name;
}





