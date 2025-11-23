package com.makibeans.dto.category;

import com.makibeans.dto.audit.AuditableInfo;

/**
 * Admin view for Category with audit metadata.
 */
public record CategoryAdminResponseDTO(
        Long id,
        String name,
        String slug,
        Long parentId,
        AuditableInfo audit
) {}
