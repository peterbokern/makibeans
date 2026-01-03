package com.makibeans.attribute.attribute.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class AttributeAdminFilter extends AttributePublicFilter {

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "id", type = { Filter.Operation.EQ, Filter.Operation.IN }, sortable = true, filterable = true)
    private List<@Positive Long> id;

    @Filter(path = "deleted", type = { Filter.Operation.EQ }, sortable = true, filterable = true)
    private Boolean deleted;

    @Filter(path = "createdAt", type = { Filter.Operation.GTE }, sortable = true, filterable = true)
    private Instant createdAtFrom;

    @Filter(path = "createdAt", type = { Filter.Operation.LTE }, sortable = true, filterable = true)
    private Instant createdAtTo;

    @Filter(path = "createdBy", type = { Filter.Operation.LIKE }, sortable = true, filterable = true)
    private String createdBy;

    @Filter(path = "updatedAt", type = { Filter.Operation.GTE }, sortable = true, filterable = true)
    private Instant updatedAtFrom;

    @Filter(path = "updatedAt", type = { Filter.Operation.LTE }, sortable = true, filterable = true)
    private Instant updatedAtTo;

    @Filter(path = "updatedBy", type = { Filter.Operation.LIKE }, sortable = true, filterable = true)
    private String updatedBy;

    @Filter(path = "deletedAt", type = { Filter.Operation.GTE }, sortable = true, filterable = true)
    private Instant deletedAtFrom;

    @Filter(path = "deletedAt", type = { Filter.Operation.LTE }, sortable = true, filterable = true)
    private Instant deletedAtTo;

    @Filter(path = "deletedBy", type = { Filter.Operation.LIKE }, sortable = true, filterable = true)
    private String deletedBy;
}
