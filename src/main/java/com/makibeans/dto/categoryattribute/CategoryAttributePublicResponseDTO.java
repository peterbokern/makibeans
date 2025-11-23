package com.makibeans.dto.categoryattribute;

/**
 * Lean public DTO for CategoryAttribute used in category detail listings.
 */
public record CategoryAttributePublicResponseDTO(
        Long id,
        Long categoryId,
        String categoryName,
        Long attributeId,
        String attributeName,
        Boolean required
) {}

