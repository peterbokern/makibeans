package com.makibeans.size.dto;

import com.makibeans.audit.dto.AuditableInfo;

/**
 * Admin view for Size including audit info.
 */
public record SizeAdminResponseDTO(
        Long id,
        String name,
        String slug,
        AuditableInfo audit
) {}
