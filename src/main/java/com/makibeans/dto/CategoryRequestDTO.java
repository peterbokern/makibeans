package com.makibeans.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryRequestDTO {

    @NotBlank(message = "Category name cannot be blank.")
    private String name;
    private String description;
    private String imageUrl;
    private Long parentCategoryId;
}
