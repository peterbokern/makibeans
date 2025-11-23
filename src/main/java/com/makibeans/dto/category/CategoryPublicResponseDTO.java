package com.makibeans.dto.category;

/**
 * Lean public DTO for Category used in listings.
 */
public record CategoryPublicResponseDTO(
        Long id,
        String name,
        String slug,
        Long parentId
) {}

