package com.makibeans.attribute.attributevalue.filter;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.makibeans.search.annotation.Filter;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Filter for AttributeValue entity.
 * Mirrors the style used for other filters (e.g., AttributeFilter, CategoryAttributeFilter).
 * IMPORTANT:
 * - Each field is bound to a single Filter.Operation (EQ, GTE, LTE, IN, LIKE, BOOL, ...)
 * - Range filters use separate *From / *To fields with path pointing to the same entity field.
 */
@Data
@JsonIgnoreProperties
public class AttributeValueFilter {

    // -------------------------------------------------------------------------
    // ID FIELDS
    // -------------------------------------------------------------------------

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(type = {Filter.Operation.IN}, sortable = true)
    private List<@Positive Long> id;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "attribute.id", type = {Filter.Operation.IN}, sortable = true)
    private List<@Positive Long> attributeId;


    // -------------------------------------------------------------------------
    // VALUE FIELDS (MUTUALLY EXCLUSIVE PER ATTRIBUTE DATA TYPE)
    // -------------------------------------------------------------------------

    /** For STRING attributes – normal LIKE search. */
    @Filter(type = {Filter.Operation.LIKE}, sortable = true)
    private String stringValue;

    /** Optional exact string match (same DB column, EQ instead of LIKE). */
    @Filter(
            type = {Filter.Operation.EQ},
            path = "stringValue",
            sortable = false
    )
    private String stringValueEq;


    /** For NUMERIC attributes – exact match on numericValue column. */
    @Filter(
            type = {Filter.Operation.EQ},
            path = "numericValue",
            sortable = true
    )
    private BigDecimal numericEq;

    /** For NUMERIC attributes – lower bound (>=) on numericValue column. */
    @Filter(
            type = {Filter.Operation.GTE},
            path = "numericValue",
            sortable = false
    )
    private BigDecimal numericValueFrom;

    /** For NUMERIC attributes – upper bound (<=) on numericValue column. */
    @Filter(
            type = {Filter.Operation.LTE},
            path = "numericValue",
            sortable = false
    )
    private BigDecimal numericValueTo;


    /** For BOOLEAN attributes. */
    @Filter(type = {Filter.Operation.EQ}, sortable = true)
    private Boolean booleanValue;


    /** For DATE attributes – exact day. */
    @Filter(type = {Filter.Operation.EQ}, sortable = true)
    private LocalDate dateValue;

    /** For DATE attributes – >=. */
    @Filter(
            type = {Filter.Operation.GTE},
            path = "dateValue",
            sortable = false
    )
    private LocalDate dateValueFrom;

    /** For DATE attributes – <=. */
   @Filter(
            type = {Filter.Operation.LTE},
            path = "dateValue",
            sortable = false
    )
    private LocalDate dateValueTo;


    /** For DATETIME attributes – exact value. */
    @Filter(type = {Filter.Operation.EQ}, sortable = true)
    private LocalDateTime dateTimeValue;

    /** For DATETIME attributes – >=. */

    @Filter(
            type = {Filter.Operation.GTE},
            path = "dateTimeValue",
            sortable = false
    )
    private LocalDateTime dateTimeValueFrom;

    /** For DATETIME attributes – <=. */
    @Filter(
            type = {Filter.Operation.LTE},
            path = "dateTimeValue",
            sortable = false
    )
    private LocalDateTime dateTimeValueTo;


    // -------------------------------------------------------------------------
    // AUDIT FIELDS
    // -------------------------------------------------------------------------

    @Filter(type = {Filter.Operation.EQ}, path = "createdAt", sortable = true)
    private Instant createdAt;

    @Filter(
            type = {Filter.Operation.GTE},
            path = "createdAt",
            sortable = false
    )
    private Instant createdAtFrom;

    @Filter(
            type = {Filter.Operation.LTE},
            path = "createdAt",
            sortable = false
    )
    private Instant createdAtTo;

    @Filter(
            type = {Filter.Operation.LIKE},
            path = "createdBy",
            sortable = true
    )
    private String createdBy;

    @Filter(
            type = {Filter.Operation.EQ},
            path = "createdBy",
            sortable = false
    )
    private String createdByEq;

    @Filter(
            type = {Filter.Operation.EQ},
            path = "updatedAt",
            sortable = true)
    private Instant updatedAt;

    @Filter(
            type = {Filter.Operation.GTE},
            path = "updatedAt",
            sortable = false
    )
    private Instant updatedAtFrom;

    @Filter(
            type = {Filter.Operation.LTE},
            path = "updatedAt",
            sortable = false
    )
    private Instant updatedAtTo;

    @Filter(
            type = {Filter.Operation.LIKE},
            path = "updatedBy",
            sortable = true
    )
    private String updatedBy;

    @Filter(
            type = {Filter.Operation.EQ},
            path = "updatedByEq",
            sortable = false
    )
    private String updatedByEq;

    @Filter(
            type = {Filter.Operation.BOOL},
            path = "deleted",
            sortable = true)
    private Boolean deleted;

    @Filter(
            type = {Filter.Operation.GTE},
            path = "deletedAt",
            sortable = false
    )
    private Instant deletedAt;

    @Filter(
            type = {Filter.Operation.GTE},
            path = "deletedAt",
            sortable = false
    )
    private Instant deletedAtFrom;

    @Filter(
            type = {Filter.Operation.LTE},
            path = "deletedAt",
            sortable = false
    )
    private Instant deletedAtTo;

    @Filter(
            type = {Filter.Operation.LIKE},
            path = "deletedBy",
            sortable = true
    )
    private String deletedBy;

    @Filter(
            type = {Filter.Operation.EQ},
            path = "deletedBy",
            sortable = false
    )
    private String deletedByEq;

    // -------------------------------------------------------------------------
    // LIFECYCLE / META FIELDS
    // -------------------------------------------------------------------------



    @Filter(type = {Filter.Operation.LIKE}, sortable = true)
    private String slug;

    @Filter(type = {Filter.Operation.EQ}, sortable = false, path = "slug")
    private String slugEq;

    @Filter(type = {Filter.Operation.EQ}, sortable = true)
    private Integer sortOrder;
}
