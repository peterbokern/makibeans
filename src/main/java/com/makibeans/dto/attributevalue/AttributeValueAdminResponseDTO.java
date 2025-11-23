package com.makibeans.dto.attributevalue;

import com.makibeans.dto.audit.AuditableInfo;

/**
 * Admin view for AttributeValue including audit metadata.
 */
public record AttributeValueAdminResponseDTO(
        Long id,
        Long attributeId,
        String attributeName,
        String value,
        AuditableInfo audit
) {}
