package com.makibeans.product.dto;

public record PriceRangeDTO(
        Long minPriceInCents,
        Long maxPriceInCents
) { }
