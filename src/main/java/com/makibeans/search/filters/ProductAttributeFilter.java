package com.makibeans.search.filters;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.Filter;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * Filter for ProductAttribute entities.
 * Compatible with SpecificationFactory + SortResolver via @Filter annotations.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class ProductAttributeFilter {

    // ----- Identity -----

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "id", type = {Filter.Operation.EQ, Filter.Operation.IN}, sortable = true, filterable = true)
    private List<@Positive Long> id;


    // ----- Product -----

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "product.id", type = {Filter.Operation.EQ, Filter.Operation.IN}, sortable = true, filterable = true)
    private List<@Positive Long> productId;

    @Filter(path = "product.name", type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = true, filterable = true)
    private String productName;


    // ----- Attribute (template) -----

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "attribute.id", type = {Filter.Operation.EQ, Filter.Operation.IN}, sortable = true, filterable = true)
    private List<@Positive Long> attributeId;

    @Filter(path = "attribute.name", type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = true, filterable = true)
    private String attributeName;


    // ----- Linked values -----

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "productAttributeValueLinks.attributeValue.id", type = {Filter.Operation.EQ, Filter.Operation.IN}, sortable = false, filterable = true)
    private List<@Positive Long> attributeValueId;

    @Filter(path = "productAttributeValueLinks.attributeValue.value", type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = false, filterable = true)
    private String attributeValue;


    // ----- Audit ranges (Auditable base) -----

    @Filter(path = "createdAt", type = {Filter.Operation.GTE}, sortable = true, filterable = true)
    private Instant createdAtFrom;

    @Filter(path = "createdAt", type = {Filter.Operation.LTE}, sortable = true, filterable = true)
    private Instant createdAtTo;

    @Filter(path = "updatedAt", type = {Filter.Operation.GTE}, sortable = true, filterable = true)
    private Instant updatedAtFrom;

    @Filter(path = "updatedAt", type = {Filter.Operation.LTE}, sortable = true, filterable = true)
    private Instant updatedAtTo;
}
