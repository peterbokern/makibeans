package com.makibeans.attribute.productattribute.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class ProductAttributePublicFilter {

    // If you keep anything public at all, restrict to product-scoped listing
    @Filter(path = "product.id", type = { Filter.Operation.EQ }, sortable = true, filterable = true)
    private Long productId;

    // Public attributes are typically only those marked visible
    @Filter(path = "visible", type = { Filter.Operation.EQ }, sortable = true, filterable = true)
    private Boolean visible;
}
