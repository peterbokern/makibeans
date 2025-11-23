package com.makibeans.productvariant.dto;

import java.math.BigDecimal;

/**
 * Public view for ProductVariant (no audit).
 * Fields mirror ProductVariantResponseDTO (adjust if your current DTO differs).
 */
public record ProductVariantPublicResponseDTO(
        Long id,
        String sku,
        String ean,
        BigDecimal price,
        Integer stock,
        Boolean active,
        Boolean defaultVariant,
        Long productId,
        String productName,
        Long sizeId,
        String sizeName
) {}
