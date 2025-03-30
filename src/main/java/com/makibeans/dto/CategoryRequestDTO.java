package com.makibeans.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Category requests.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequestDTO {

    @NotBlank(message = "Category name cannot be blank.")
    @Size(min = 3, max = 50, message = "Category name must be between 3 and 50 characters.")
    private String name;
    @Size(max = 255, message = "Description must be less than 255 characters.")
    private String description;
    @Size(max = 255, message = "Image URL must be less than 255 characters.")
    private String imageUrl;
    private Long parentCategoryId;
}
