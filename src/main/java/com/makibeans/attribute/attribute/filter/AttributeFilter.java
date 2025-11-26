package com.makibeans.attribute.attribute.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * Filter for Attribute entity.
 * Used in SearchRequest<AttributeFilter> to dynamically build JPA Specifications.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class AttributeFilter {

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(type = {Filter.Operation.EQ, Filter.Operation.IN}, sortable = true)
    private List<@Positive Long> id;

    @Filter(type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = true)
    private String name;

    @Filter(type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = true)
    private String description;

    @Filter(type = {Filter.Operation.EQ, Filter.Operation.IN}, path = "template.id", sortable = true)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<@Positive Long> templateId;

    @Filter(type = {Filter.Operation.BOOL}, sortable = true)
    private Boolean required;

    @Filter(type = {Filter.Operation.GTE, Filter.Operation.LTE}, sortable = true)
    private Instant createdAt;

    @Filter(type = {Filter.Operation.GTE, Filter.Operation.LTE}, sortable = true)
    private Instant updatedAt;
}
