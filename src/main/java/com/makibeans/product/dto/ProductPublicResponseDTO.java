package com.makibeans.product.dto;

import com.makibeans.category.dto.CategoryRefDTO;
import com.makibeans.productattribute.dto.ProductAttributePublicResponseDTO;
import com.makibeans.productvariant.dto.ProductVariantPublicResponseDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class ProductPublicResponseDTO {
    Long id;
    String name;
    String slug;
    String description;
    String imageUrl;
    CategoryRefDTO category;
    PriceRangeDTO price;
    List<ProductVariantPublicResponseDTO> variants;
    List<ProductAttributePublicResponseDTO> attributes;
}
