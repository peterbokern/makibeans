package com.makibeans.categoryattribute.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryAttributeUpdateDTO {

    private Boolean isRequired;
    private Boolean filterable;
}
