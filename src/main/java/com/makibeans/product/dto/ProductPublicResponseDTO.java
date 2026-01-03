package com.makibeans.product.dto;

import com.makibeans.attribute.productattribute.dto.ProductAttributePublicResponseDTO;
import com.makibeans.productvariant.dto.ProductVariantPublicResponseDTO;
import com.makibeans.productvariant.dto.ProductVariantResponseDTO;

import java.util.List;

/**
 * Public view for Product (no audit).
 */
public record ProductPublicResponseDTO(
        Long id,
        String name,
        String slug,
        String description,
        String imageUrl,
        Long categoryId,
        String categoryName,
        List<ProductVariantPublicResponseDTO> productVariants,
        List<ProductAttributePublicResponseDTO> productAttributes
) {}
