package com.makibeans.product.dto;

import com.makibeans.audit.dto.AuditableInfo;
import com.makibeans.attribute.productattribute.dto.ProductAttributeAdminResponseDTO;
import com.makibeans.productvariant.dto.ProductVariantAdminResponseDTO;
import com.makibeans.productvariant.dto.ProductVariantResponseDTO;

import java.util.List;

/**
 * Admin view for Product, includes audit info.
 */
public record ProductAdminResponseDTO(
        Long id,
        String name,
        String slug,
        String description,
        String imageUrl,
        Long categoryId,
        String categoryName,
        List<ProductVariantAdminResponseDTO> productVariants,
        List<ProductAttributeAdminResponseDTO> productAttributes,
        AuditableInfo audit
) {}
