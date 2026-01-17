package com.makibeans.category.filter.dto;

public record PriceFilterDefinitionDTO(
        Long minPriceInCents,
        Long maxPriceInCents
) { }
