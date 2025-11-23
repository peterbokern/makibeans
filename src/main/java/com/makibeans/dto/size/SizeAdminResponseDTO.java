package com.makibeans.dto.size;

import com.makibeans.dto.audit.AuditableInfo;

/**
 * Admin view for Size including audit info.
 */
public record SizeAdminResponseDTO(
        Long id,
        String name,
        AuditableInfo audit
) {}
