package com.makibeans.dto.categoryattribute;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryAttributeRequestDTO {

    @NotNull(message = "Category ID cannot be null.")
    private Long categoryId;

    @NotNull(message = "Attribute ID cannot be null.")
    private Long attributeId;

    private Boolean required;

}
