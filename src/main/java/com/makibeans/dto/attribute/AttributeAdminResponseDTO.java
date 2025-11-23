package com.makibeans.dto.attribute;

import com.makibeans.dto.audit.AuditableInfo;

/**
 * Admin view for Attribute including audit fields.
 */
public record AttributeAdminResponseDTO(
        Long id,
        String name,
        String description,
        AuditableInfo audit
) {}
