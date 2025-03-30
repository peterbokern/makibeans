package com.makibeans.mapper;

import com.makibeans.dto.ProductResponseDTO;
import com.makibeans.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductResponseDTO}.
 */

@Mapper(componentModel = "spring", uses = {ProductVariantMapper.class, ProductAttributeMapper.class})
public interface ProductMapper {

    /**
     * Converts a Product entity to a ProductResponseDTO.
     *
     * @param entity the Product entity to convert
     * @return the converted ProductResponseDTO
     */

    @Mapping(source = "productVariants", target = "productVariants")
    @Mapping(source = "productAttributes", target = "productAttributes")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ProductResponseDTO toResponseDTO(Product entity);
}
