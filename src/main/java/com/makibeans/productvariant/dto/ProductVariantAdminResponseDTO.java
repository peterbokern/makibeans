package com.makibeans.productvariant.dto;

import com.makibeans.audit.dto.AuditableInfo;

import java.math.BigDecimal;

/**
 * Admin view for ProductVariant including audit info.
 */
public record ProductVariantAdminResponseDTO(
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
        String sizeName,
        AuditableInfo audit
) {}
