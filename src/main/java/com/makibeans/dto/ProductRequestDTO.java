package com.makibeans.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {
    @NotBlank(message = "Product name cannot be blank.")
    private String productName;

    @NotBlank(message = "Product description cannot be blank.")
    private String productDescription;

    private String productImageUrl;

    @NotNull(message = "Category id should not be null")
    private Long categoryId;
}
