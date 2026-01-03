package com.makibeans.productvariant.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class ProductVariantPublicFilter {

    // If you ever keep a public endpoint, only allow listing variants by product
    @Filter(path = "product.id", type = { Filter.Operation.EQ }, sortable = true, filterable = true)
    private Long productId;
}
