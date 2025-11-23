package com.makibeans.category.dto;

import java.util.List;

/**
 * Lean public DTO for Category used in listings.
 */
public record CategoryPublicResponseDTO(
        Long id,
        String name,
        String description,
        String imageUrl,
        Long parentCategoryId,

        List<CategoryPublicResponseDTO> subCategories,
        List<BreadCrumbDTO> breadCrumbs
) {}

