
package com.makibeans.dto.category;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating a Category.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryUpdateDTO {

    @Size(min = 3, max = 50, message = "Category name must be between 3 and 50 characters.")
    private String name;

    @Size(max = 255, message = "Description must be less than 255 characters.")
    private String description;

    private Long parentCategoryId;
}
