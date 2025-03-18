package com.makibeans.dto;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {

    private Long id;
    private String productName;
    private String productDescription;
    private String productImageUrl;
    private Long categoryId;
    private String category;
    private List<ProductVariantResponseDTO> productVariants;
    private List<ProductAttributeResponseDTO> productAttributes;
}
