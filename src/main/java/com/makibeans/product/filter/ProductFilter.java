// java
package com.makibeans.product.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class ProductFilter {

    /* ---------- Core ---------- */

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "id", type = {Filter.Operation.EQ, Filter.Operation.IN}, sortable = true, filterable = true)
    private List<@Positive Long> id;

    @Filter(path = "name", type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = true, filterable = true)
    private String name;

    @Filter(path = "slug", type = { Filter.Operation.LIKE }, sortable = true)
    private String slug;

    @Filter(path = "description", type = {Filter.Operation.LIKE}, sortable = false, filterable = true)
    private String description;

    @Filter(path = "image", type = {Filter.Operation.BOOL}, sortable = false, filterable = true)
    private Boolean hasImage;


    /* ---------- Category ---------- */

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "category.id", type = {Filter.Operation.EQ, Filter.Operation.IN}, sortable = true, filterable = true)
    private List<@Positive Long> categoryId;

    @Filter(path = "category.name", type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = true, filterable = true)
    private String categoryName;


    /* ---------- Audit (Auditable) ---------- */

    @Filter(path = "createdAt", type = {Filter.Operation.GTE}, sortable = true, filterable = true)
    private Instant createdAtFrom;

    @Filter(path = "createdAt", type = {Filter.Operation.LTE}, sortable = true, filterable = true)
    private Instant createdAtTo;

    @Filter(path = "updatedAt", type = {Filter.Operation.GTE}, sortable = true, filterable = true)
    private Instant updatedAtFrom;

    @Filter(path = "updatedAt", type = {Filter.Operation.LTE}, sortable = true, filterable = true)
    private Instant updatedAtTo;


    /* ---------- Variants (joins: productVariants.*) ---------- */

    @Filter(path = "productVariants.priceInCents", type = {Filter.Operation.GTE}, sortable = true, filterable = true)
    private Long minPriceInCents;

    @Filter(path = "productVariants.priceInCents", type = {Filter.Operation.LTE}, sortable = true, filterable = true)
    private Long maxPriceInCents;

    @Filter(path = "productVariants.stock", type = {Filter.Operation.GTE}, sortable = true, filterable = true)
    private Long minStock;

    @Filter(path = "productVariants.stock", type = {Filter.Operation.LTE}, sortable = true, filterable = true)
    private Long maxStock;

    @Filter(path = "productVariants.sku", type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = true, filterable = true)
    private String variantSku;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "productVariants.size.id", type = {Filter.Operation.EQ, Filter.Operation.IN}, sortable = true, filterable = true)
    private List<@Positive Long> sizeId;

    @Filter(path = "productVariants.size.name", type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = true, filterable = true)
    private String sizeName;


    /* ---------- Attributes (joins: productAttributes.*) ---------- */

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "productAttributes.categoryAttribute.attribute.id", type = {Filter.Operation.EQ, Filter.Operation.IN}, sortable = true, filterable = true)
    private List<@Positive Long> attributeId;

    @Filter(path = "productAttributes.categoryAttribute.attribute.name", type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = true, filterable = true)
    private String attributeName;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "productAttributes.productAttributeValues.attributeValue.id", type = {Filter.Operation.EQ, Filter.Operation.IN}, sortable = false, filterable = true)
    private List<@Positive Long> attributeValueId;

    @Filter(path = "productAttributes.productAttributeValues.attributeValue.slug", type = {Filter.Operation.LIKE, Filter.Operation.EQ}, sortable = false, filterable = true)
    private String attributeSlug;
}
