package com.makibeans.categoryattribute.dto;

import com.makibeans.audit.dto.AuditableInfo;

/**
 * Admin view for CategoryAttribute including audit metadata.
 */
public record CategoryAttributeAdminResponseDTO(
        Long categoryAttributeId,
/*        Long categoryId,
        String categoryName,*/
        Long attributeId,
        String attributeName,
        Boolean required,
        Boolean filterable,
        Integer sortOrder,
        AuditableInfo audit
) {}