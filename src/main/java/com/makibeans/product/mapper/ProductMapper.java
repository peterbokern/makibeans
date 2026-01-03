package com.makibeans.product.mapper;

import com.makibeans.attribute.productattribute.mapper.ProductAttributeMapper;
import com.makibeans.audit.mapper.AuditableMapper;
import com.makibeans.productvariant.mapper.ProductVariantMapper;
import com.makibeans.product.dto.ProductAdminResponseDTO;
import com.makibeans.product.dto.ProductPublicResponseDTO;
import com.makibeans.product.dto.ProductUpdateDTO;
import com.makibeans.product.model.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {ProductVariantMapper.class, ProductAttributeMapper.class, AuditableMapper.class})
public interface ProductMapper {

    // --- Public DTO mapping ---

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(source = ".", target = "imageUrl", qualifiedByName = "getImageUrl")
    @Mapping(source = "slug", target = "slug")
    ProductPublicResponseDTO toPublicResponseDTO(Product entity);

    // --- Admin DTO mapping with audit ---

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = ".", target = "imageUrl", qualifiedByName = "getImageUrl")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "audit", source = ".")
    @Mapping(source = "slug", target = "slug")
    ProductAdminResponseDTO toAdminResponseDTO(Product entity);

    @Named("getImageUrl")
    default String getImageUrl(Product product) {
        return product.getImage() != null
                ? "/products/" + product.getId() + "/image"
                : "null";
    }

    @Named("trimDescription")
    default String trimDescription(String description) {
        return description != null ? description.trim() : null;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", qualifiedByName = "trimDescription")
    void updateEntityFromDTO(ProductUpdateDTO updateDTO, @MappingTarget Product product);
}
