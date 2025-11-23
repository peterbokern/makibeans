package com.makibeans.attribute.dto;

import com.makibeans.audit.dto.AuditableInfo;

/**
 * Admin view for Attribute including audit fields.
 */
public record AttributeAdminResponseDTO(
        Long id,
        String name,
        String description,
        AuditableInfo audit
) {}
