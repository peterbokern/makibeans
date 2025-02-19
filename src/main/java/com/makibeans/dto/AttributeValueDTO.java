package com.makibeans.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttributeValueDTO {

    private Long id;

    @NotNull(message = "Template ID cannot be null.")
    private Long templateId;

    @NotBlank(message = "Attribute value cannot be blank.")
    private String value;
}
