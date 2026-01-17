package com.makibeans.category.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class CategoryPublicFilter {

    // Optional: storefront search/autocomplete (usually redundant with ?search=)
    @Filter(path = "name", type = { Filter.Operation.LIKE }, sortable = true, filterable = true)
    private String name;

    @Filter(path = "slug", type = { Filter.Operation.EQ }, sortable = true, filterable = true)
    private String slug;
}
