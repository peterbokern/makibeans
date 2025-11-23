package com.makibeans.dto.categoryattribute;

/**
 * Lean public DTO for CategoryAttribute used in category detail listings.
 */
public record CategoryAttributePublicResponseDTO(
        Long id,
        Long categoryId,
        Long attributeId,
        String attributeName
) {}

