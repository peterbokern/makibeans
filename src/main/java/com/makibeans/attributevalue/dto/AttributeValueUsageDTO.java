package com.makibeans.attributevalue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AttributeValueUsageDTO(
        boolean usedInProductAttributeValues
){
    @JsonProperty("inUse")
    public boolean inUse() {
        return usedInProductAttributeValues;
    }
}
