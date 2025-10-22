package com.makibeans.search.filters;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.Filter;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class ProductVariantFilter {

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "id", type = {Filter.Operation.EQ, Filter.Operation.IN}, sortable = true, filterable = true)
    private List<@Positive Long> id;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "product.id", type = {Filter.Operation.EQ, Filter.Operation.IN}, sortable = true, filterable = true)
    private List<@Positive Long> productId;

    @Filter(path = "product.name", type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = true, filterable = true)
    private String productName;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "size.id", type = {Filter.Operation.EQ, Filter.Operation.IN}, sortable = true, filterable = true)
    private List<@Positive Long> sizeId;

    @Filter(path = "size.name", type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = true, filterable = true)
    private String sizeName;

    @Filter(path = "sku", type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = true, filterable = true)
    private String sku;

    @Filter(path = "priceInCents", type = {Filter.Operation.GTE}, sortable = true, filterable = true)
    private Long minPriceInCents;

    @Filter(path = "priceInCents", type = {Filter.Operation.LTE}, sortable = true, filterable = true)
    private Long maxPriceInCents;

    @Filter(path = "stock", type = {Filter.Operation.GTE}, sortable = true, filterable = true)
    private Long minStock;

    @Filter(path = "stock", type = {Filter.Operation.LTE}, sortable = true, filterable = true)
    private Long maxStock;

    @Filter(path = "createdAt", type = {Filter.Operation.GTE}, sortable = true, filterable = true)
    private Instant createdAtFrom;

    @Filter(path = "createdAt", type = {Filter.Operation.LTE}, sortable = true, filterable = true)
    private Instant createdAtTo;

    @Filter(path = "updatedAt", type = {Filter.Operation.GTE}, sortable = true, filterable = true)
    private Instant updatedAtFrom;

    @Filter(path = "updatedAt", type = {Filter.Operation.LTE}, sortable = true, filterable = true)
    private Instant updatedAtTo;
}
