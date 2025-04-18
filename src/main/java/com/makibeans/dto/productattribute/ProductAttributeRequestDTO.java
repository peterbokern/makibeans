package com.makibeans.dto.productattribute;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for ProductAttribute requests.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductAttributeRequestDTO {
    @NotNull(message = "Product id cannot be null.")
    private Long productId;

    @NotNull(message = "Template id cannot be null.")
    private Long templateId;
}
