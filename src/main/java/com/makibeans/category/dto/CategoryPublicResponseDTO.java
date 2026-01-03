package com.makibeans.category.dto;

import java.util.List;

/**
 * Lean public DTO for Category used in listings.
 */
public record CategoryPublicResponseDTO(
        Long id,
        String name,
        String slug,
        String description,
        String imageUrl,
        CategoryRefDTO parent,
        List<CategoryRefDTO> breadcrumbs
) {}

