package com.makibeans.attribute.dto;

/**
 * Lean public DTO for Attribute (catalog/admin-light).
 */
public record AttributePublicResponseDTO(
        Long id,
        String name,
        String description
) {}

