package com.makibeans.mapper;

import com.makibeans.dto.productattributevalue.ProductAttributeValueAdminResponseDTO;
import com.makibeans.dto.productattributevalue.ProductAttributeValuePublicResponseDTO;
import com.makibeans.dto.productattributevalue.ProductAttributeValueResponseDTO;
import com.makibeans.model.ProductAttributeValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductAttributeValueMapper {

    // -------------------------------------------------------------------------
    // Public DTO
    // -------------------------------------------------------------------------

    @Mapping(source = "id",                               target = "id")
    @Mapping(source = "productAttribute.id",              target = "productAttributeId")
    @Mapping(source = "productAttribute.product.id",      target = "productId")
    @Mapping(source = "productAttribute.product.name",    target = "productName")
    @Mapping(source = "productAttribute.attribute.id",    target = "attributeId")
    @Mapping(source = "productAttribute.attribute.name",  target = "attributeName")
    @Mapping(source = "attributeValue.id",                target = "attributeValueId")
    @Mapping(source = "attributeValue.value",             target = "value")
    ProductAttributeValuePublicResponseDTO toPublicResponseDTO(ProductAttributeValue entity);

    // -------------------------------------------------------------------------
    // Admin DTO (with audit)
    // -------------------------------------------------------------------------

    @Mapping(source = "id",                               target = "id")
    @Mapping(source = "productAttribute.id",              target = "productAttributeId")
    @Mapping(source = "productAttribute.product.id",      target = "productId")
    @Mapping(source = "productAttribute.product.name",    target = "productName")
    @Mapping(source = "productAttribute.attribute.id",    target = "attributeId")
    @Mapping(source = "productAttribute.attribute.name",  target = "attributeName")
    @Mapping(source = "attributeValue.id",                target = "attributeValueId")
    @Mapping(source = "attributeValue.value",             target = "value")
    @Mapping(target = "audit", expression = "java(com.makibeans.mapper.AuditableMapper.toAuditableInfo(entity))")
    ProductAttributeValueAdminResponseDTO toAdminResponseDTO(ProductAttributeValue entity);
}
