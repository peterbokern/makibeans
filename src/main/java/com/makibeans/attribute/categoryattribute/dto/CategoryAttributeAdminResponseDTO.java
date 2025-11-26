package com.makibeans.attribute.categoryattribute.dto;

import com.makibeans.audit.dto.AuditableInfo;

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