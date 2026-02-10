package com.makibeans.product.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Data
@Getter
@JsonIgnoreProperties(ignoreUnknown = false)
public class ProductPublicFilter implements ProductFilterBase {

    /**
     * Generic search input for product listing pages (name/description/category).
     * Implement this in your SearchFilter/full-text logic, not as separate LIKE fields here.
     */
    private String query;

    /**
     * Usually only useful for exact lookups (product detail pages), not for broad filtering,
     * but keeping it doesn't hurt and is clean.
     */
    @Filter(path = "slug", type = { Filter.Operation.EQ }, sortable = false, filterable = true)
    private String slug;

    /* ---------- Category ---------- */

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "category.id", type = { Filter.Operation.EQ, Filter.Operation.IN }, sortable = false, filterable = true)
    private List<@Positive Long> categoryId;

    /* ---------- Variants ---------- */

    //handled by ProductSearchService
    private @Min(0) Long minPriceInCents;

    private @Min(0) Long  maxPriceInCents;

    private Boolean inStock;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<@Positive Long> sizeId;

    private String sku;

    /* ---------- Attributes (dynamic) ---------- */

    /**
     * Dynamic attribute filtering, e.g.:
     * origin=ethiopia,kenya
     * roast-level=light-roast,dark-roast
     *
     * Key should be the Attribute  slug
     * Values should be AttributeValue slugs
     */
    private Map<String, List<String>> attributeFilters;
}
