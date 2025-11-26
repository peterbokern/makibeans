package com.makibeans.attribute.attributevalue.dto;

import com.makibeans.audit.dto.AuditableInfo;

/**
 * Admin view for AttributeValue including audit metadata.
 */
public record AttributeValueAdminResponseDTO(
        Long id,
        Long attributeId,
        String attributeName,
        String value,
        String dataType,
        AuditableInfo audit
) {}
