package com.makibeans.productvariant.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import lombok.Data;

import java.time.Instant;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class ProductVariantAdminFilter {

    @Filter(path = "sku", type = { Filter.Operation.LIKE, Filter.Operation.EQ }, sortable = true, filterable = true)
    private String sku;

    @Filter(path = "priceInCents", type = { Filter.Operation.GTE }, sortable = true, filterable = true)
    private Long minPriceInCents;

    @Filter(path = "priceInCents", type = { Filter.Operation.LTE }, sortable = true, filterable = true)
    private Long maxPriceInCents;

    @Filter(path = "stock", type = { Filter.Operation.GTE }, sortable = true, filterable = true)
    private Long minStock;

    @Filter(path = "stock", type = { Filter.Operation.LTE }, sortable = true, filterable = true)
    private Long maxStock;

    @Filter(path = "deleted", type = { Filter.Operation.EQ }, sortable = true, filterable = true)
    private Boolean deleted;

    @Filter(path = "createdAt", type = { Filter.Operation.GTE }, sortable = true, filterable = true)
    private Instant createdAtFrom;

    @Filter(path = "createdAt", type = { Filter.Operation.LTE }, sortable = true, filterable = true)
    private Instant createdAtTo;

    @Filter(path = "updatedAt", type = { Filter.Operation.GTE }, sortable = true, filterable = true)
    private Instant updatedAtFrom;

    @Filter(path = "updatedAt", type = { Filter.Operation.LTE }, sortable = true, filterable = true)
    private Instant updatedAtTo;
}
