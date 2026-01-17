package com.makibeans.productvariant.dto;

import com.makibeans.size.dto.SizePublicResponseDTO;

import java.math.BigDecimal;

/**
 * Public view for ProductVariant (no audit).
 * Fields mirror ProductVariantResponseDTO (adjust if your current DTO differs).
 */
public record ProductVariantPublicResponseDTO(
        Long id,
        String sku,
        Long priceInCents,
        Integer stock,
        boolean isDefault,
        SizePublicResponseDTO size
) {}
