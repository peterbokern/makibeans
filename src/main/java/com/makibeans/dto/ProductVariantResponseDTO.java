package com.makibeans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for ProductVariant responses.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantResponseDTO {
    private Long id;
    private Long sizeId;
    private String sizeName;
    private String sku;
    private Long priceInCents;
    private Long stock;
}
