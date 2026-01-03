package com.makibeans.attribute.productattribute.mapper;

import com.makibeans.attribute.productattribute.dto.ProductAttributeAdminResponseDTO;
import com.makibeans.attribute.productattribute.dto.ProductAttributePublicResponseDTO;
import com.makibeans.attribute.productattribute.model.ProductAttribute;
import com.makibeans.attribute.productattributevalue.mapper.ProductAttributeValueMapper;
import com.makibeans.audit.mapper.AuditableMapper;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {AuditableMapper.class, ProductAttributeValueMapper.class})
public interface ProductAttributeMapper {


    // ------------ PUBLIC ------------
    @Mapping(source = "product.id",                    target = "productId")
    @Mapping(source = "product.name",                  target = "productName")
    @Mapping(source = "categoryAttribute.id",          target = "categoryAttributeId")
    @Mapping(source = "categoryAttribute.attribute.id",   target = "attributeId")
    @Mapping(source = "categoryAttribute.attribute.name", target = "attributeName")
    @Mapping(target ="values", source="productAttributeValues")
    ProductAttributePublicResponseDTO toPublicResponseDTO(ProductAttribute productAttribute);

    // ------------ ADMIN ------------
    @Mapping(source = "product.id",                    target = "productId")
    @Mapping(source = "product.name",                  target = "productName")
    @Mapping(source = "categoryAttribute.id",          target = "categoryAttributeId")
    @Mapping(source = "categoryAttribute.attribute.id",   target = "attributeId")
    @Mapping(source = "categoryAttribute.attribute.name", target = "attributeName")
    @Mapping(target ="values", source="productAttributeValues")
    @Mapping(target = "audit", source = ".")
    ProductAttributeAdminResponseDTO toAdminResponseDTO(ProductAttribute productAttribute);

}
