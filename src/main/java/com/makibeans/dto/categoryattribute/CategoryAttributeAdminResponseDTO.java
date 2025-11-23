package com.makibeans.dto.categoryattribute;

import com.makibeans.dto.audit.AuditableInfo;

/**
 * Admin view for CategoryAttribute including audit metadata.
 */
public record CategoryAttributeAdminResponseDTO(
        Long id,
        Long categoryId,
        Long attributeId,
        String attributeName,
        AuditableInfo audit
) {}
