package com.makibeans.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(min = 1, max = 255, message = "Attribute value must be between 1 and 255 characters.")
    private String value;
}
