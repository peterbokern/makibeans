package com.makibeans.dto.productvariant;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import lombok.*;

/**
 * Data Transfer Object for ProductVariant requests.
 */

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
    @Digits(integer = 10, fraction = 0, message = "Price must be a valid number with up to 10 digits.")
    private Long priceInCents;

    @NotNull(message = "Stock cannot be null.")
    @Min(value = 0, message = "Stock should be a minimum of 0.")
    @Digits(integer = 10, fraction = 0, message = "Stock must be a valid number with up to 10 digits.")
    private Long stock;
}
