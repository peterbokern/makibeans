package com.makibeans.dto.productvariant;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;

import lombok.*;

/**
 * Data Transfer Object for ProductVariant updates.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantUpdateDTO{

    @Min(value = 0, message = "Price should be a minimum of 0.")
    @Digits(integer = 10, fraction = 0, message = "Price must be a valid number with up to 10 digits.")
    private Long priceInCents;

    @Min(value = 0, message = "Stock should be a minimum of 0.")
    @Digits(integer = 10, fraction = 0, message = "Stock must be a valid number with up to 10 digits.")
    private Long stock;
}


