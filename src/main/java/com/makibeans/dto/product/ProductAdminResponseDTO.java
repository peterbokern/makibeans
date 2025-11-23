package com.makibeans.dto.product;

import com.makibeans.dto.productattribute.ProductAttributePublicResponseDTO;
import com.makibeans.dto.productvariant.ProductVariantPublicResponseDTO;
import com.makibeans.dto.audit.AuditableInfo;

import java.util.List;

/**
 * Admin view for Product including audit fields and nested public children.
 * Use only in admin endpoints or when caller is authorized.
 */
public record ProductAdminResponseDTO(
        Long id,
        String name,
        String description,
        String imageUrl,
        Long categoryId,
        String categoryName,
        List<ProductVariantPublicResponseDTO> productVariants,
        List<ProductAttributePublicResponseDTO> productAttributes,
        AuditableInfo audit
) {}
