package com.makibeans.category.dto;

import com.makibeans.audit.dto.AuditableInfo;

import java.util.List;

/**
 * Admin view for Category with audit metadata.
 */
public record CategoryAdminResponseDTO(
        Long id,
        String name,
        String slug,
        String description,
        String imageUrl,
        CategoryRefDTO parent,
        List<CategoryRefDTO> breadcrumbs,
        AuditableInfo audit
) {}
