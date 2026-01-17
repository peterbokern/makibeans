package com.makibeans.productattribute.filter;

import com.makibeans.search.annotation.Filter;
import lombok.Data;

@Data
public class ProductAttributeFilter {

    @Filter(type = { Filter.Operation.EQ }, path = "product.id")
    private Long productId;

    @Filter(type = { Filter.Operation.EQ }, path = "attribute.id")
    private Long attributeId;

    @Filter(type = { Filter.Operation.EQ })
    private Boolean deleted;
}
