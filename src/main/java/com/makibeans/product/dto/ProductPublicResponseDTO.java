package com.makibeans.product.dto;

import com.makibeans.dto.productattribute.ProductAttributePublicResponseDTO;
import com.makibeans.dto.productvariant.ProductVariantResponseDTO;

import java.util.List;

/**
 * Public view for Product (no audit).
 */
public record ProductPublicResponseDTO(
        Long id,
        String name,
        String description,
        String imageUrl,
        Long categoryId,
        String categoryName,
        List<ProductVariantResponseDTO> productVariants,
        List<ProductAttributePublicResponseDTO> productAttributes
) {}
