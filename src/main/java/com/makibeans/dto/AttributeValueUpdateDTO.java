package com.makibeans.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AttributeValueUpdateDTO {

    @NotBlank(message = "Attribute value cannot be blank.")
    private String value;
}
