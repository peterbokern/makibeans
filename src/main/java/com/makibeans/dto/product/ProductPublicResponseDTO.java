package com.makibeans.dto.product;

/**
 * Lean public product DTO used for catalog/list endpoints.
 * Keep it minimal to reduce payload size.
 */
public record ProductPublicResponseDTO(
        Long id,
        String name,
        String description,
        String imageUrl,
        Long categoryId,
        String categoryName
) {}

