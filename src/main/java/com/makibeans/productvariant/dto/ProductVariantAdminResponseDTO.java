package com.makibeans.productvariant.dto;

import com.makibeans.audit.dto.AuditableInfo;
import com.makibeans.size.dto.SizePublicResponseDTO;

import java.math.BigDecimal;

/**
 * Admin view for ProductVariant including audit info.
 */
public record ProductVariantAdminResponseDTO(
        Long id,
        String sku,
        Long priceInCents,
        Integer stock,
        boolean isDefault,
        SizePublicResponseDTO size,
        AuditableInfo audit
) {}
