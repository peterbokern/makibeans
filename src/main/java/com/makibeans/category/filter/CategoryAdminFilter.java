package com.makibeans.category.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class CategoryAdminFilter extends CategoryPublicFilter {

    @Filter(path = "description", type = { Filter.Operation.LIKE }, sortable = false, filterable = true)
    private String description;

    @Filter(path = "createdAt", sortable = true, filterable = true)
    private Instant createdAt;

    @Filter(type = { Filter.Operation.GTE }, path = "createdAt", sortable = true, filterable = true)
    private Instant createdAtFrom;

    @Filter(type = { Filter.Operation.LTE }, path = "createdAt", sortable = true, filterable = true)
    private Instant createdAtTo;

    @Filter(type = { Filter.Operation.LIKE }, path = "createdBy", sortable = false, filterable = true)
    private String createdBy;

    @Filter( path = "updatedAt", sortable = true, filterable = true)
    private Instant updatedAt;

    @Filter(type = { Filter.Operation.GTE }, path = "updatedAt", sortable = false, filterable = true)
    private Instant updatedAtFrom;

    @Filter(type = { Filter.Operation.LTE }, path = "updatedAt", sortable = false, filterable = true)
    private Instant updatedAtTo;

    @Filter(type = { Filter.Operation.LIKE }, path = "updatedBy", sortable = false, filterable = true)
    private String updatedBy;

    @Filter(path = "deletedAt", sortable = true, filterable = true)
    private Instant deletedAt;

    @Filter(type = { Filter.Operation.GTE }, path = "deletedAt", sortable = false, filterable = true)
    private Instant deletedAtFrom;

    @Filter(type = { Filter.Operation.LTE }, path = "deletedAt", sortable = false, filterable = true)
    private Instant deletedAtTo;

    @Filter(type = { Filter.Operation.LIKE }, path = "deletedBy", sortable = false, filterable = true)
    private String deletedBy;
}

