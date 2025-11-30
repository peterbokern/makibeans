package com.makibeans.attribute.productattributevalue.mapper;

import com.makibeans.attribute.productattributevalue.dto.ProductAttributeValueAdminResponseDTO;
import com.makibeans.attribute.productattributevalue.dto.ProductAttributeValuePublicResponseDTO;
import com.makibeans.attribute.productattributevalue.model.ProductAttributeValue;
import com.makibeans.audit.mapper.AuditableMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AuditableMapper.class})
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
    @Mapping(target = "value", expression = "java(entity.getAttributeValue() != null ? entity.getAttributeValue().getValueAsString() : null)")
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
    @Mapping(target = "value", expression = "java(entity.getAttributeValue() != null ? entity.getAttributeValue().getValueAsString() : null)")
    @Mapping(target = "audit", source = ".")
    ProductAttributeValueAdminResponseDTO toAdminResponseDTO(ProductAttributeValue entity);

    // Custom mapping method to handle Object to String conversion
    default String map(Object value) {
        return value != null ? value.toString() : null;
    }
}
