package com.makibeans.mapper;

import com.makibeans.dto.ProductResponseDTO;
import com.makibeans.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

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
    @Mapping(source = "category.name", target = "name")
    @Mapping(source = "category.description", target = "description")
    @Mapping(source= ".", target = "imageUrl", qualifiedByName = "getImageUrl")
    ProductResponseDTO toResponseDTO(Product entity);

    /**
     * Returns the image URL of the given product.
     *
     * @param product the product to get the image URL from
     * @return the image URL of the given product
     */

    @Named("getImageUrl")
    default String getImageUrl(Product product) {
        return product.getProductImage() != null
                ? "/products/" + product.getId() + "/image"
                : "null";
    }
}
