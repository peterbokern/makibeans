package com.makibeans.attribute.categoryattribute.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class CategoryAttributeFilter {

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "category.id", type = {Filter.Operation.IN}, sortable = true)
    private List<@Positive Long> categoryId;

    @Filter(path = "category.name", type = {Filter.Operation.LIKE}, sortable = true)
    private String categoryName;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "attribute.id", type = {Filter.Operation.IN}, sortable = true)
    private List<@Positive Long> attributeId;

    @Filter(path = "attribute.name", type = {Filter.Operation.LIKE}, sortable = true)
    private String attributeName;

    @Filter(path = "required", type = {Filter.Operation.BOOL}, sortable = true)
    private Boolean required;

    // Range example (createdAt)
    @Filter(path = "createdAt", type = {Filter.Operation.GTE})
    private Instant createdAtFrom;

    @Filter(path = "createdAt", type = {Filter.Operation.LTE})
    private Instant createdAtTo;
}
