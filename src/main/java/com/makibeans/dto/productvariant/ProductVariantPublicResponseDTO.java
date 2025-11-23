package com.makibeans.dto.productvariant;

/**
 * Lean public representation of a product variant used in listings and product detail.
 */
public record ProductVariantPublicResponseDTO(
        Long id,
        String sku,
        String name,
        Double price,
        Integer stock,
        String imageUrl
) {}

