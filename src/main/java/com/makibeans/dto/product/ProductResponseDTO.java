package com.makibeans.dto.product;

import com.makibeans.dto.productvariant.ProductVariantResponseDTO;
import com.makibeans.dto.productattribute.ProductAttributeResponseDTO;
import lombok.*;
import java.util.List;

/**
 * Data Transfer Object for ProductResponse.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    private List<ProductVariantResponseDTO> productVariants;
    private List<ProductAttributeResponseDTO> productAttributes;
}
