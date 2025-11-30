package com.makibeans.productvariant.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for ProductVariant responses.
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "sizeId", "sizeName", "sku", "priceInCents", "stock", "createdBy", "createdAt", "updatedBy", "updatedAt"})
public class ProductVariantResponseDTO {
    private Long id;
    private Long sizeId;
    private String sizeName;
    private String sku;
    private Long priceInCents;
    private Long stock;
}
