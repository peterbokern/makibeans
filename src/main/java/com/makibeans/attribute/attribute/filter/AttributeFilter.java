package com.makibeans.attribute.attribute.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.attribute.attribute.model.AttributeDataType;
import com.makibeans.attribute.attribute.model.AttributeInputType;
import com.makibeans.search.annotation.Filter;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * Filter for Attribute entity.
 * Used in SearchRequest<AttributeFilter> to dynamically build JPA Specifications.
 *
 * Query params use the Java field names (no custom keys / aliases):
 * - id, name, dataType, inputType, active, deleted, createdAtFrom/To, updatedAtFrom/To
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeFilter {

    // -------------------------------------------------------------------------
    // ID / BASIC FIELDS
    // -------------------------------------------------------------------------

    @Filter(type = {Filter.Operation.IN}, sortable = true)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<@Positive Long> id;

    @Filter(type = {Filter.Operation.LIKE}, sortable = true)
    private String name;

    @Filter(type = {Filter.Operation.EQ}, path = "name", sortable = true)
    private String nameEq;

    @Filter(type = {Filter.Operation.LIKE}, sortable = true)
    private String description;

    @Filter(type = {Filter.Operation.EQ}, path = "description", sortable = true)
    private String descriptionEq;

    @Filter(type = {Filter.Operation.EQ}, path = "slug", sortable = true)
    private String slug;

    @Filter(type = {Filter.Operation.EQ}, path = "slug", sortable = true)
    private String slugEq;

    @Filter(type = {Filter.Operation.EQ, Filter.Operation.IN}, path = "dataType", sortable = true)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<AttributeDataType> dataType;

    @Filter(type = {Filter.Operation.IN}, path = "inputType", sortable = true)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<AttributeInputType> inputType;

    @Filter(type = {Filter.Operation.BOOL}, path = "active", sortable = true)
    private Boolean active;

    @Filter(type = {Filter.Operation.BOOL}, path = "deleted", sortable = true)
    private Boolean deleted;

    // -------------------------------------------------------------------------
    // AUDIT RANGE FILTERS
    // -------------------------------------------------------------------------

    @Filter(type = {Filter.Operation.GTE}, path = "createdAt", sortable = true)
    private Instant createdAtFrom;

    @Filter(type = {Filter.Operation.LTE}, path = "createdAt", sortable = true)
    private Instant createdAtTo;

    @Filter(type = {Filter.Operation.GTE}, path = "updatedAt", sortable = true)
    private Instant updatedAtFrom;

    @Filter(type = {Filter.Operation.LTE}, path = "updatedAt", sortable = true)
    private Instant updatedAtTo;
}
