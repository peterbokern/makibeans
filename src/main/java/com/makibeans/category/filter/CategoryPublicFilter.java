package com.makibeans.category.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class CategoryPublicFilter {

    // For category pages (/categories/{slug})
    @Filter(path = "slug", type = { Filter.Operation.EQ }, sortable = true, filterable = true)
    private String slug;

    // For category navigation trees
    @Filter(path = "parentCategory.id", type = { Filter.Operation.EQ }, sortable = true, filterable = true)
    private Long parentId;

    // Optional: storefront search/autocomplete (usually redundant with ?search=)
    @Filter(path = "name", type = { Filter.Operation.LIKE }, sortable = true, filterable = true)
    private String name;
}
