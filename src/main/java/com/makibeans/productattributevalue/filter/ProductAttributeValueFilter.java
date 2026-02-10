package com.makibeans.productattributevalue.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class ProductAttributeValueFilter {

    @Filter(path = "productAttribute.id", type = { Filter.Operation.EQ }, sortable = true, filterable = true)
    private Long productAttributeId;

    @Filter(path = "attributeValue.id", type = { Filter.Operation.EQ }, sortable = true, filterable = true)
    private Long attributeValueId;

    @Filter(path = "deleted", type = { Filter.Operation.EQ }, sortable = true, filterable = true)
    private Boolean deleted;
}
