package com.makibeans.dto.product;

import com.makibeans.dto.audit.AuditableInfo;
import com.makibeans.dto.productattribute.ProductAttributeAdminResponseDTO;
import com.makibeans.dto.productvariant.ProductVariantResponseDTO;
import com.makibeans.dto.productattribute.ProductAttributeResponseDTO;

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
