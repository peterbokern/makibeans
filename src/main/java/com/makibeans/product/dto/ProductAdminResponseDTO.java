package com.makibeans.product.dto;

import com.makibeans.audit.dto.AuditableInfo;
import com.makibeans.category.dto.CategoryRefDTO;
import com.makibeans.product.repository.PriceRange;
import com.makibeans.productattribute.dto.ProductAttributePublicResponseDTO;
import com.makibeans.productvariant.dto.ProductVariantPublicResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class ProductAdminResponseDTO {
    Long id;
    String name;
    String slug;
    String description;
    String imageUrl;
    CategoryRefDTO category;
    PriceRangeDTO price;
    List<ProductVariantPublicResponseDTO> variants;
    List<ProductAttributePublicResponseDTO> attributes;
    AuditableInfo audit;
}
