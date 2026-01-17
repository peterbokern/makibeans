package com.makibeans.productattributevalue.dto;

import com.makibeans.audit.dto.AuditableInfo;

/**
 * Admin view for ProductAttributeValue including audit info.
 */
public record ProductAttributeValueAdminResponseDTO(
        Long productAttributeValueId,
        Long attributeValueId,
        String value,
        String valueSlug,
        // Audit block (createdBy/At, updatedBy/At, etc.)
        AuditableInfo audit
) {}