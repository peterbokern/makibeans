package com.makibeans.product.dto;

import com.makibeans.audit.dto.AuditableInfo;
import com.makibeans.productattribute.dto.ProductAttributeAdminResponseDTO;
import com.makibeans.productvariant.dto.ProductVariantResponseDTO;

import java.util.List;

/**
 * Admin view for Product, includes audit info.
 */
public record ProductAdminResponseDTO(
        Long id,
        String name,
        String description,
        String imageUrl,
        Long categoryId,
        String categoryName,
        List<ProductVariantResponseDTO> productVariants,
        List<ProductAttributeAdminResponseDTO> productAttributes,
        AuditableInfo audit
) {}
