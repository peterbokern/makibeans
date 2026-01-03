package com.makibeans.attribute.categoryattribute.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class CategoryAttributePublicFilter {

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "category.id", type = { Filter.Operation.EQ, Filter.Operation.IN }, sortable = true, filterable = true)
    private List<@Positive Long> categoryId;

    // Optional (storefront might filter to show only visible facets)
    @Filter(path = "attribute.visible", type = { Filter.Operation.EQ }, sortable = true, filterable = true)
    private Boolean attributeVisible;
}
