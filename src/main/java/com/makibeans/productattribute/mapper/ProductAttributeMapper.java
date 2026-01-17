package com.makibeans.productattribute.mapper;

import com.makibeans.attribute.mapper.AttributeMapper;
import com.makibeans.productattribute.dto.ProductAttributeAdminResponseDTO;
import com.makibeans.productattribute.dto.ProductAttributePublicResponseDTO;
import com.makibeans.productattribute.model.ProductAttribute;
import com.makibeans.productattributevalue.mapper.ProductAttributeValueMapper;
import com.makibeans.audit.mapper.AuditableMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AuditableMapper.class, AttributeMapper.class, ProductAttributeValueMapper.class})
public interface ProductAttributeMapper {


    // ------------ PUBLIC ------------
    @Mapping(source = "categoryAttribute.id",          target = "categoryAttributeId")
    @Mapping(source = "categoryAttribute.attribute.id",   target = "attributeId")
    @Mapping(source = "categoryAttribute.attribute.name", target = "attributeName")
    @Mapping(source= "categoryAttribute.attribute.slug", target = "attributeSlug")
    @Mapping(target = "dataType",  expression = "java(productAttribute.getCategoryAttribute().getAttribute().getInputType().getLabel())")
    @Mapping(target = "inputType", expression = "java(productAttribute.getCategoryAttribute().getAttribute().getInputType().getLabel())")
    @Mapping(target ="values", source="productAttributeValues")
    ProductAttributePublicResponseDTO toPublicResponseDTO(ProductAttribute productAttribute);

    // ------------ ADMIN ------------

    @Mapping(source = "categoryAttribute.id",          target = "categoryAttributeId")
    @Mapping(source = "categoryAttribute.attribute.id",   target = "attributeId")
    @Mapping(source = "categoryAttribute.attribute.name", target = "attributeName")
    @Mapping(source= "categoryAttribute.attribute.slug", target = "attributeSlug")
    @Mapping(target = "dataType",  expression = "java(productAttribute.getCategoryAttribute().getAttribute().getInputType().getLabel())")
    @Mapping(target = "inputType", expression = "java(productAttribute.getCategoryAttribute().getAttribute().getInputType().getLabel())")
    @Mapping(target ="values", source="productAttributeValues")
    @Mapping(target = "audit", source = ".")
    ProductAttributeAdminResponseDTO toAdminResponseDTO(ProductAttribute productAttribute);

}
