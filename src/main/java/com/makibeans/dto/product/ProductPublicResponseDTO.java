package com.makibeans.dto.product;

import com.makibeans.dto.productattribute.ProductAttributePublicResponseDTO;
import com.makibeans.dto.productvariant.ProductVariantResponseDTO;
import com.makibeans.dto.productattribute.ProductAttributeResponseDTO;

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
