package com.makibeans.attribute.attribute.dto;

/**
 * Lean public DTO for Attribute (catalog/admin-light).
 */
public record AttributePublicResponseDTO(
        Long id,
        String name,
        String description,
        String dataType,
        String inputType,
        String slug,
        boolean active
) {}

