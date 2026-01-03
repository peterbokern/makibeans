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

    // Deleted flag (admin toggles or audit views)
    @Filter(type = { Filter.Operation.EQ }, path = "deleted", sortable = true, filterable = true)
    private Boolean deleted;

    // CREATED
    @Filter(type = { Filter.Operation.GTE }, path = "createdAt", sortable = true, filterable = true)
    private Instant createdAtFrom;

    @Filter(type = { Filter.Operation.LTE }, path = "createdAt", sortable = true, filterable = true)
    private Instant createdAtTo;

    @Filter(type = { Filter.Operation.LIKE }, path = "createdBy", sortable = true, filterable = true)
    private String createdBy;

    // UPDATED
    @Filter(type = { Filter.Operation.GTE }, path = "updatedAt", sortable = true, filterable = true)
    private Instant updatedAtFrom;

    @Filter(type = { Filter.Operation.LTE }, path = "updatedAt", sortable = true, filterable = true)
    private Instant updatedAtTo;

    @Filter(type = { Filter.Operation.LIKE }, path = "updatedBy", sortable = true, filterable = true)
    private String updatedBy;

    // DELETED (optional, if you store deletedAt/deletedBy in your Auditable)
    @Filter(type = { Filter.Operation.GTE }, path = "deletedAt", sortable = true, filterable = true)
    private Instant deletedAtFrom;

    @Filter(type = { Filter.Operation.LTE }, path = "deletedAt", sortable = true, filterable = true)
    private Instant deletedAtTo;

    @Filter(type = { Filter.Operation.LIKE }, path = "deletedBy", sortable = true, filterable = true)
    private String deletedBy;
}
