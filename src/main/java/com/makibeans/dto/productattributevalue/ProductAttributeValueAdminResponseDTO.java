package com.makibeans.dto.productattributevalue;

import com.makibeans.dto.audit.AuditableInfo;

/**
 * Admin view for ProductAttributeValue mapping, includes audit fields.
 */
public record ProductAttributeValueAdminResponseDTO(
        Long id,
        Long productAttributeId,
        Long attributeValueId,
        String value,
        AuditableInfo audit
) {}
