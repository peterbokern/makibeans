package com.makibeans.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Product requests.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {
    @NotBlank(message = "Product name cannot be blank.")
    private String name;

    @NotBlank(message = "Product description cannot be blank.")
    private String description;

/*    private String productImageUrl;*/

    @NotNull(message = "Category id should not be null")
    private Long categoryId;
}
