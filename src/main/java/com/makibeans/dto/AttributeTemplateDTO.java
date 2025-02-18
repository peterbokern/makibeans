package com.makibeans.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttributeTemplateDTO {

    @NotBlank(message = "Name of attribute template cannot be blank.")
    private String name;
}





