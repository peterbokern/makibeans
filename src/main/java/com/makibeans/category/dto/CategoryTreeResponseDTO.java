package com.makibeans.category.dto;

import java.util.List;

public record CategoryTreeResponseDTO(
        Long id,
        String name,
        String slug,
        String imageUrl,
        List<CategoryTreeResponseDTO> subCategories
) {}
