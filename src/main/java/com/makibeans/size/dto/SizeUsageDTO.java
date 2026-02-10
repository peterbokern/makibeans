package com.makibeans.size.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SizeUsageDTO(
        boolean usedByProductVariants
) {
    @JsonProperty("inUse")
    public boolean isInUse() {
        return usedByProductVariants;
    }
}
