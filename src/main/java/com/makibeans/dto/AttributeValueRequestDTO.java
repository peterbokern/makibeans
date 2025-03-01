package com.makibeans.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeValueRequestDTO {

    @NotNull(message = "Template ID cannot be null.")
    private Long templateId;

    @NotBlank(message = "Attribute value cannot be blank.")
    private String value;
}
