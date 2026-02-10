package com.makibeans.product.repository;

public interface PriceRange {
    Long getProductId();
    Long getMinPriceInCents();
    Long getMaxPriceInCents();
}
