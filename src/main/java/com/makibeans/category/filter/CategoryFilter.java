package com.makibeans.category.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * Filter DTO for Category search.
 * Fields annotated with {@link Filter} define which operations are supported
 * and which fields are sortable. GET and POST both use this same DTO.
 *
 * Notes:
 * - Lists accept single values in JSON (POST) due to ACCEPT_SINGLE_VALUE_AS_ARRAY.
 * - Free-text "search" (in SearchRequest) will OR-match all fields that declare LIKE here.
 * - Deleted rows are excluded by default at service level; not exposed as a filter.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class CategoryFilter {

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "id", type = { Filter.Operation.IN }, sortable = true)
    private List<@Positive Long> id;

    @Filter(path = "name", type = { Filter.Operation.LIKE }, sortable = true)
    private String name;

    @Filter(path = "slug", type = { Filter.Operation.LIKE }, sortable = true)
    private String slug;

    // -------------------------------------------------------------------------
    // AUDIT FIELDS
    // -------------------------------------------------------------------------

    // Created-at range
    @Filter(type = { Filter.Operation.GTE }, path = "createdAt", sortable = false)
    private Instant createdAtFrom;

    @Filter(type = { Filter.Operation.LTE }, path = "createdAt", sortable = false)
    private Instant createdAtTo;

    @Filter(type = { Filter.Operation.LIKE }, path = "createdBy", sortable = true)
    private String createdBy;

    @Filter(type = { Filter.Operation.EQ }, path = "createdBy", sortable = false)
    private String createdByEq;

    // Updated-at range
    @Filter(type = { Filter.Operation.GTE }, path = "updatedAt", sortable = false)
    private Instant updatedAtFrom;

    @Filter(type = { Filter.Operation.LTE }, path = "updatedAt", sortable = false)
    private Instant updatedAtTo;

    @Filter(type = { Filter.Operation.LIKE }, path = "updatedBy", sortable = true)
    private String updatedBy;

    @Filter(type = { Filter.Operation.EQ }, path = "updatedBy", sortable = false)
    private String updatedByEq;

    // Deleted flags / range
    @Filter(type = { Filter.Operation.BOOL }, path = "deleted", sortable = true)
    private Boolean deleted;

    @Filter(type = { Filter.Operation.GTE }, path = "deletedAt", sortable = false)
    private Instant deletedAtFrom;

    @Filter(type = { Filter.Operation.LTE }, path = "deletedAt", sortable = false)
    private Instant deletedAtTo;

    @Filter(type = { Filter.Operation.LIKE }, path = "deletedBy", sortable = true)
    private String deletedBy;

    @Filter(type = { Filter.Operation.EQ }, path = "deletedBy", sortable = false)
    private String deletedByEq;
}
