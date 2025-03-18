package com.makibeans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantResponseDTO {
    private Long id;
    private Long sizeId;
    private String sizeName;
    private Long priceInCents;
    private String sku;
    private Long stock;
}
