package com.makibeans.dto.categoryattribute;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryAttributeUpdateDTO {

    private Boolean isRequired;

    @Min(value = 0, message = "Sort order must be a non-negative integer.")
    private Integer sortOrder;
}
