package com.makibeans.attribute.productattribute.dto;

import com.makibeans.audit.dto.AuditableInfo;

/**
 * Admin view for ProductAttribute including audit metadata.
 */
public record ProductAttributeAdminResponseDTO(
        Long id,
        Long productId,
        String productName,
        Long attributeId,
        String attributeName,
        AuditableInfo audit
) {}
