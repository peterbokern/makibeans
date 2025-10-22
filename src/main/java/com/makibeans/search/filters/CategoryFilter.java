package com.makibeans.search.filters;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.Filter;
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
    @Filter(path = "id", type = { Filter.Operation.EQ, Filter.Operation.IN }, sortable = true)
    private List<@Positive Long> id;

    @Filter(path = "name", type = { Filter.Operation.LIKE }, sortable = true)
    private String name;

    @Filter(path = "slug", type = { Filter.Operation.LIKE }, sortable = true)
    private String slug;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "parent.id", type = { Filter.Operation.EQ, Filter.Operation.IN }, sortable = true)
    private List<@Positive Long> parentId;

    // Created-at range
    @Filter(path = "createdAt", type = { Filter.Operation.GTE })
    private Instant createdAtFrom;

    @Filter(path = "createdAt", type = { Filter.Operation.LTE })
    private Instant createdAtTo;
}
