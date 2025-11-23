package com.makibeans.dto.productvariant;

import com.makibeans.dto.audit.AuditableInfo;

/**
 * Admin view for ProductVariant with audit metadata.
 */
public record ProductVariantAdminResponseDTO(
        Long id,
        String sku,
        String name,
        Double price,
        Integer stock,
        String imageUrl,
        AuditableInfo audit
) {}
