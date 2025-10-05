package com.makibeans.dto.product;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.makibeans.dto.audit.AuditableResponseDTO;
import com.makibeans.dto.productvariant.ProductVariantResponseDTO;
import com.makibeans.dto.productattribute.ProductAttributeResponseDTO;
import lombok.*;
import java.util.List;

/**
 * Data Transfer Object for ProductResponse.
 */

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "name", "description", "imageUrl", "categoryId", "categoryName", "productVariants", "productAttributes", "createdBy", "createdAt", "updatedBy", "updatedAt"})
public class ProductResponseDTO extends AuditableResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    private List<ProductVariantResponseDTO> productVariants;
    private List<ProductAttributeResponseDTO> productAttributes;
}
