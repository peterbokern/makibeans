package com.makibeans.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttributeValueUpdateDTO {

    @NotBlank(message = "Attribute value cannot be blank.")
    private String value;
}
