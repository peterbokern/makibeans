package com.makibeans.category.dto;

import com.makibeans.audit.dto.AuditableInfo;

import java.util.List;

/**
 * Admin view for Category with audit metadata.
 */
public record CategoryAdminResponseDTO(
        Long id,
        String name,
        String description,
        String imageUrl,
        Long parentCategoryId,

        List<CategoryAdminResponseDTO>subCategories,
        List<BreadCrumbDTO> breadCrumbs,
        AuditableInfo audit
) {}
