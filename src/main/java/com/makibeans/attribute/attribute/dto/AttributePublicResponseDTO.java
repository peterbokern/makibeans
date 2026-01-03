package com.makibeans.attribute.attribute.dto;

import com.makibeans.attribute.attributevalue.dto.AttributeValuePublicResponseDTO;

import java.util.List;

/**
 * Lean public DTO for Attribute (catalog/admin-light).
 */
public record AttributePublicResponseDTO(
        Long id,
        String name,
        String description,
        String dataType,
        String inputType,
        String slug
) {}

