package com.makibeans.attribute.attributevalue.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * Filter for AttributeValue entity.
 * Mirrors the style used for other filters (e.g., AttributeFilter, CategoryAttributeFilter).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class AttributeValueFilter {

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(type = {Filter.Operation.EQ, Filter.Operation.IN}, sortable = true)
    private List<@Positive Long> id;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "attribute.id", type = {Filter.Operation.EQ, Filter.Operation.IN}, sortable = true)
    private List<@Positive Long> attributeId;

    @Filter(type = {Filter.Operation.LIKE}, sortable = true)
    private String value;

    @Filter(type = {Filter.Operation.GTE, Filter.Operation.LTE}, sortable = true)
    private Instant createdAt;

    @Filter(type = {Filter.Operation.GTE, Filter.Operation.LTE}, sortable = true)
    private Instant updatedAt;

    @Filter(type = {Filter.Operation.BOOL}, sortable = true)
    private Boolean deleted;
}
