package com.makibeans.dto.product;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating a Product.
 * Contains fields for product name, description, image URL, and category ID.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDTO {

    @Size(min = 3, max = 100)
    private String name;

    @Size(min = 5, max = 1000)
    private String description;

    @Size(max = 1000)
    private String productImageUrl;

    private Long categoryId;
}
