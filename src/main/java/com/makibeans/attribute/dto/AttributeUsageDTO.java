package com.makibeans.attribute.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO representing how an {@code Attribute} is used across the system.
 * <p>
 * The {@link #inUse()} flag is derived from the other three flags and is not
 * stored as a separate component to avoid inconsistencies.
 */
public record AttributeUsageDTO(
        boolean usedInValues,
        boolean usedInCategoryAttributes
) {
    /**
     * Indicates whether the attribute is used anywhere.
     *
     * @return {@code true} if used in values, product attributes or category attributes
     */
    @JsonProperty("inUse")
    public boolean inUse() {
        return usedInValues || usedInCategoryAttributes;
    }
}
