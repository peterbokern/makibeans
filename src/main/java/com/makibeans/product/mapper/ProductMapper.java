package com.makibeans.product.mapper;

import com.makibeans.productattribute.mapper.ProductAttributeMapper;
import com.makibeans.productvariant.mapper.ProductVariantMapper;
import com.makibeans.product.dto.ProductAdminResponseDTO;
import com.makibeans.product.dto.ProductPublicResponseDTO;
import com.makibeans.product.dto.ProductResponseDTO;
import com.makibeans.product.dto.ProductUpdateDTO;
import com.makibeans.product.model.Product;
import org.mapstruct.*;

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

    // --- Public DTO mapping ---

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = ".", target = "imageUrl", qualifiedByName = "getImageUrl")
    ProductPublicResponseDTO toPublicResponseDTO(Product entity);

    // --- Admin DTO mapping with audit ---

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = ".", target = "imageUrl", qualifiedByName = "getImageUrl")
    @Mapping(target = "audit", expression = "java(com.makibeans.audit.mapper.AuditableMapper.toAuditableInfo(entity))")
    ProductAdminResponseDTO toAdminResponseDTO(Product entity);

    @Named("getImageUrl")
    default String getImageUrl(Product product) {
        return product.getImage() != null
                ? "/products/" + product.getId() + "/image"
                : "null";
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(ProductUpdateDTO updateDTO, @MappingTarget Product product);
}
