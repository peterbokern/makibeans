package com.makibeans.search.filters;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.Filter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class UserFilter {

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "id", type = {Filter.Operation.EQ, Filter.Operation.IN}, sortable = true, filterable = true)
    private List<@Positive Long> id;

    @Email
    @Filter(path = "email", type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = true, filterable = true)
    private String email;

    @Filter(path = "firstName", type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = true, filterable = true)
    private String firstName;

    @Filter(path = "lastName", type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = true, filterable = true)
    private String lastName;

    @Filter(path = "enabled", type = {Filter.Operation.EQ}, sortable = true, filterable = true)
    private Boolean enabled;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "roles.id", type = {Filter.Operation.EQ, Filter.Operation.IN}, sortable = false, filterable = true)
    private List<@Positive Long> roleId;

    @Filter(path = "createdAt", type = {Filter.Operation.GTE}, sortable = true, filterable = true)
    private Instant createdAtFrom;

    @Filter(path = "createdAt", type = {Filter.Operation.LTE}, sortable = true, filterable = true)
    private Instant createdAtTo;

    @Filter(path = "updatedAt", type = {Filter.Operation.GTE}, sortable = true, filterable = true)
    private Instant updatedAtFrom;

    @Filter(path = "updatedAt", type = {Filter.Operation.LTE}, sortable = true, filterable = true)
    private Instant updatedAtTo;
}
