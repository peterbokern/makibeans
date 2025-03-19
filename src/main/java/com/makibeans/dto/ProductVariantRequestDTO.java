package com.makibeans.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantRequestDTO {

    @NotNull(message = "Product ID cannot be null.")
    private Long productId;

    @NotNull(message = "Size ID cannot be null.")
    private Long sizeId;

    @NotNull(message = "Price cannot be null.")
    @Min(value = 0, message = "Price should be a minimum of 0.")
    private Long priceInCents;

    @NotNull(message = "Stock cannot be null.")
    @Min(value = 0, message = "Stock should be a minimum of 0.")
    private Long stock;
}
