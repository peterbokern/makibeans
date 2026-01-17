package com.makibeans.category.filter.dto;

import java.util.List;

public record ProductFilterDefinitionsDTO(
        PriceFilterDefinitionDTO price,
        List<SizeFilterDefinitionDTO> sizes,
        boolean inStockFilterEnabled
) { }
