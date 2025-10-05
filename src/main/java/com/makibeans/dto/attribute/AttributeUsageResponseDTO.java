package com.makibeans.dto.attribute;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for Attribute Usage response.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeUsageResponseDTO {
    private Long attributeId;
    private String attributeName;
    private boolean inUse;
    private UsageSummary usageSummary;
    private List<ValueUsage> valueUsage;
    private List<ProductUsage> productUsage;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsageSummary {
        private Long totalValues;
        private Long totalCategories;
        private Long totalProducts;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValueUsage {
        private Long valueId;
        private String valueName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryUsage {
        private Long categoryId;
        private String categoryName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductUsage {
        private Long productId;
        private String productName;
    }
}

