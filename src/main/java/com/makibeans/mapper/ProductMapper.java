package com.makibeans.mapper;

import com.makibeans.dto.ProductResponseDTO;
import com.makibeans.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Map;

@Mapper(componentModel = "spring", uses = {ProductVariantMapper.class, ProductAttributeMapper.class})
public interface ProductMapper {

    @Mapping(source = "productVariants", target = "productVariants")
    @Mapping(source = "productAttributes", target = "productAttributes")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ProductResponseDTO toResponseDTO(Product entity);

}
