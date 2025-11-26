package com.makibeans.product.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.makibeans.audit.dto.AuditableResponseDTO;
import com.makibeans.productvariant.dto.ProductVariantResponseDTO;
import com.makibeans.attribute.productattribute.dto.ProductAttributeResponseDTO;
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
