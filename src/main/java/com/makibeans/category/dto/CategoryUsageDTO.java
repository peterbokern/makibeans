package com.makibeans.category.dto;

public record CategoryUsageDTO(
        boolean usedInProductAttributes,
        boolean usedInProducts,
        boolean usedInChildCategories
) {
    public boolean inUse() {
        return usedInProductAttributes || usedInProducts || usedInChildCategories;
    }
}
