package com.makibeans.attribute.categoryattribute.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CategoryAttributeUsageDTO(
        boolean usedByProductAttributes
) {
    @JsonProperty("inUse")
    public boolean isInUse() {
        return usedByProductAttributes;
    }
}
