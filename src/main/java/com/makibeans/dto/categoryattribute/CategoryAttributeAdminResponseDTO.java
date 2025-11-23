package com.makibeans.dto.categoryattribute;

import com.makibeans.dto.audit.AuditableInfo;

/**
 * Admin view for CategoryAttribute including audit metadata.
 */
public record CategoryAttributeAdminResponseDTO(
        Long id,
        Long categoryId,
        String categoryName,
        Long attributeId,
        String attributeName,
        Boolean required,
        AuditableInfo audit
) {}