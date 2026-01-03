package com.makibeans.attribute.categoryattribute.dto;


import jakarta.validation.constraints.Min;
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

    @Min(value = 0, message = "Sort order must be zero or a positive integer.")
    private Integer sortOrder ;

    private Boolean required;

}
