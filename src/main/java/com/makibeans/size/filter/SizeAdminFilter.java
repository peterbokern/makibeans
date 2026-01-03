package com.makibeans.size.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class SizeAdminFilter extends SizePublicFilter {

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
