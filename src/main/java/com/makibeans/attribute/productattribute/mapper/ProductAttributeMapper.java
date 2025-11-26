package com.makibeans.attribute.productattribute.mapper;

import com.makibeans.attribute.productattribute.dto.ProductAttributeAdminResponseDTO;
import com.makibeans.attribute.productattribute.dto.ProductAttributePublicResponseDTO;
import com.makibeans.attribute.productattribute.dto.ProductAttributeResponseDTO;
import com.makibeans.attribute.productattribute.model.ProductAttribute;
import com.makibeans.attribute.attributevalue.dto.AttributeValueSimpleResponseDTO;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for the entity {@link ProductAttribute} and its DTO {@link ProductAttributeResponseDTO}.
 */

@Mapper(componentModel = "spring")
public interface ProductAttributeMapper {

/*    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "attribute.id", target = "attributeId")
    @Mapping(source = "attribute.name", target = "attributeName")
    @Mapping(source = ".", target = "values", qualifiedByName = "mapAttributeValues")
    ProductAttributeResponseDTO toResponseDTO(ProductAttribute entity);*/

    // ------------ PUBLIC ------------
    @Mapping(source = "product.id",     target = "productId")
    @Mapping(source = "product.name",   target = "productName")
    @Mapping(source = "attribute.id",   target = "attributeId")
    @Mapping(source = "attribute.name", target = "attributeName")
    ProductAttributePublicResponseDTO toPublicResponseDTO(ProductAttribute productAttribute);

    // ------------ ADMIN ------------
    @Mapping(source = "product.id",     target = "productId")
    @Mapping(source = "product.name",   target = "productName")
    @Mapping(source = "attribute.id",   target = "attributeId")
    @Mapping(source = "attribute.name", target = "attributeName")
    @Mapping(target = "audit", expression = "java(com.makibeans.audit.mapper.AuditableMapper.toAuditableInfo(productAttribute))")
    ProductAttributeAdminResponseDTO toAdminResponseDTO(ProductAttribute productAttribute);


/*    @Named("mapAttributeValues")
    default Set<AttributeValueSimpleResponseDTO> mapAttributeValues(ProductAttribute productAttribute) {
        return productAttribute.getProductAttributeValues()
                .stream()
                .map(productattributeValue -> new AttributeValueSimpleResponseDTO(productattributeValue.getAttributeValue().getId(), productattributeValue.getAttributeValue().getValue()))
                .collect(Collectors.toSet());
    }*/

}
