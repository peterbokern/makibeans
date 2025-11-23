package com.makibeans.search.filters;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.Filter;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class ProductAttributeValueFilter {

    // ----- Identity -----
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "id", type = {Filter.Operation.IN}, sortable = true, filterable = true)
    private List<@Positive Long> id;

    // ----- Link owner (ProductAttribute) -----
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "productAttribute.id", type = {Filter.Operation.IN}, sortable = true, filterable = true)
    private List<@Positive Long> productAttributeId;

    // Product info via productAttribute.product
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "productAttribute.product.id", type = {Filter.Operation.IN}, sortable = true, filterable = true)
    private List<@Positive Long> productId;

    @Filter(path = "productAttribute.product.name", type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = true, filterable = true)
    private String productName;

    // Attribute info via productAttribute.attribute
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "productAttribute.attribute.id", type = {Filter.Operation.IN}, sortable = true, filterable = true)
    private List<@Positive Long> attributeId;

    @Filter(path = "productAttribute.attribute.name", type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = true, filterable = true)
    private String attributeName;

    // ----- Value side (AttributeValue) -----
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "attributeValue.id", type = {Filter.Operation.EQ, Filter.Operation.IN}, sortable = true, filterable = true)
    private List<@Positive Long> attributeValueId;

    @Filter(path = "attributeValue.value", type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = true, filterable = true)
    private String value;

    // ----- Audit ranges -----
    @Filter(path = "createdAt", type = {Filter.Operation.GTE}, sortable = true, filterable = true)
    private Instant createdAtFrom;

    @Filter(path = "createdAt", type = {Filter.Operation.LTE}, sortable = true, filterable = true)
    private Instant createdAtTo;

    @Filter(path = "updatedAt", type = {Filter.Operation.GTE}, sortable = true, filterable = true)
    private Instant updatedAtFrom;

    @Filter(path = "updatedAt", type = {Filter.Operation.LTE}, sortable = true, filterable = true)
    private Instant updatedAtTo;
}
