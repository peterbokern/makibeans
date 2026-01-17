package com.makibeans.category.filter.dto;

import java.util.List;

public record CategoryFilterDefinitionsResponseDTO(
        Long categoryId,
        String categoryName,
        String slug,
        List<AttributeFilterDefinitionDTO> attributeFilters,
        ProductFilterDefinitionsDTO productFilters
) { }
