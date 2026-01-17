package com.makibeans.attributevalue.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import lombok.Data;

import java.time.Instant;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class AttributeValueAdminFilter {

    @Filter(path = "id", type = { Filter.Operation.EQ, Filter.Operation.IN }, sortable = true, filterable = true)
    private java.util.List<Long> id;

    @Filter(path = "value", type = { Filter.Operation.LIKE }, sortable = true, filterable = true)
    private String value;

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
