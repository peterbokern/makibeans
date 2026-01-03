package com.makibeans.attribute.attributevalue.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makibeans.search.annotation.Filter;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class AttributeValuePublicFilter {

    // Facet values are always fetched per attribute/template
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @Filter(path = "attribute.id", type = { Filter.Operation.EQ, Filter.Operation.IN }, sortable = true, filterable = true)
    private List<@Positive Long> attributeId;

    @Filter(path = "slug", type = { Filter.Operation.EQ }, sortable = true, filterable = true)
    private String slug;

    // Optional: searching inside facet values (e.g. “Bra” -> “Brazil”)
    @Filter(path = "value", type = { Filter.Operation.LIKE }, sortable = true, filterable = true)
    private String value;
}
