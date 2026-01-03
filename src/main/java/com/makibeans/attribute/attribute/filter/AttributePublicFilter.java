package com.makibeans.attribute.attribute.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class AttributePublicFilter {

    @Filter(path = "slug", type = { Filter.Operation.EQ }, sortable = true, filterable = true)
    private String slug;

    @Filter(path = "name", type = { Filter.Operation.LIKE }, sortable = true, filterable = true)
    private String name;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "dataType", type = { Filter.Operation.EQ, Filter.Operation.IN }, sortable = true, filterable = true)
    private List<String> dataType;

    @Filter(path = "visible", type = { Filter.Operation.EQ }, sortable = true, filterable = true)
    private Boolean visible;
}
