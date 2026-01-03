package com.makibeans.user.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class UserFilter {

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "id", type = { Filter.Operation.EQ, Filter.Operation.IN }, sortable = true, filterable = true)
    private List<@Positive Long> id;

    @Filter(path = "username", type = { Filter.Operation.LIKE, Filter.Operation.EQ }, sortable = true, filterable = true)
    private String username;

    @Filter(path = "email", type = { Filter.Operation.LIKE, Filter.Operation.EQ }, sortable = true, filterable = true)
    private String email;

    @Filter(path = "enabled", type = { Filter.Operation.EQ }, sortable = true, filterable = true)
    private Boolean enabled;

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
