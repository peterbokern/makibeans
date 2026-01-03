package com.makibeans.productvariant.filter;

import com.makibeans.search.annotation.Filter;
import lombok.Data;

@Data
public class ProductVariantFilter {

    @Filter(type = { Filter.Operation.EQ }, path = "product.id")
    private Long productId;

    @Filter(type = { Filter.Operation.LIKE })
    private String sku;

    @Filter(type = { Filter.Operation.GTE })
    private Integer stockFrom;

    @Filter(type = { Filter.Operation.LTE })
    private Integer stockTo;

    @Filter(type = { Filter.Operation.EQ })
    private Boolean deleted;
}
