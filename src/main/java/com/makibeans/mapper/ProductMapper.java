package com.makibeans.mapper;

import com.makibeans.dto.product.ProductResponseDTO;
import com.makibeans.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductResponseDTO}.
 */

@Mapper(componentModel = "spring", uses = {ProductVariantMapper.class, ProductAttributeMapper.class})
public interface ProductMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "productVariants", target = "productVariants")
    @Mapping(source = "productAttributes", target = "productAttributes")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = ".", target = "imageUrl", qualifiedByName = "getImageUrl")
    ProductResponseDTO toResponseDTO(Product entity);

    @Named("getImageUrl")
    default String getImageUrl(Product product) {
        return product.getImage() != null
                ? "/products/" + product.getId() + "/image"
                : "null";
    }
}
