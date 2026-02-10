package com.makibeans.product.filter;

import java.util.List;
import java.util.Map;

public interface ProductFilterBase {
    Map<String, List<String>> getAttributeFilters();
    List<Long> getCategoryId();
    Boolean getInStock();
    Long getMinPriceInCents();
    Long getMaxPriceInCents();
    String getSku();
    List<Long> getSizeId();
}
