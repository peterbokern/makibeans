package com.makibeans.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeTemplateRequestDTO {

    @NotBlank(message = "Name of attribute template cannot be blank.")
    private String name;
}





