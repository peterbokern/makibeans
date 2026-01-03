package com.makibeans.product.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class ProductPublicFilter {

    // Product detail routing usually uses /products/{id}, but slug filtering is useful too
    @Filter(path = "slug", type = { Filter.Operation.EQ }, sortable = true, filterable = true)
    private String slug;

    // Category page filtering
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "category.id", type = { Filter.Operation.EQ, Filter.Operation.IN }, sortable = true, filterable = true)
    private List<@Positive Long> categoryId;

    // Price range (OK to keep; this is a normal storefront filter)
    @Filter(path = "productVariants.priceInCents", type = { Filter.Operation.GTE }, sortable = false, filterable = true)
    private Long minPriceInCents;

    @Filter(path = "productVariants.priceInCents", type = { Filter.Operation.LTE }, sortable = false, filterable = true)
    private Long maxPriceInCents;

    // Optional storefront filters
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "productVariants.size.id", type = { Filter.Operation.EQ, Filter.Operation.IN }, sortable = false, filterable = true)
    private List<@Positive Long> sizeId;

    // Stock/availability (only if your query builder handles it correctly)
    @Filter(path = "productVariants.stock", type = { Filter.Operation.GTE }, sortable = false, filterable = true)
    private Long minStock; // storefront “in stock” can be minStock=1
}
