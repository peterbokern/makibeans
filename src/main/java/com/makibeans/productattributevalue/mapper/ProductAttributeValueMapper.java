package com.makibeans.productattributevalue.mapper;

import com.makibeans.productattributevalue.dto.ProductAttributeValueAdminResponseDTO;
import com.makibeans.productattributevalue.dto.ProductAttributeValuePublicResponseDTO;
import com.makibeans.productattributevalue.model.ProductAttributeValue;
import com.makibeans.audit.mapper.AuditableMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AuditableMapper.class})
public interface ProductAttributeValueMapper {

    // -------------------------------------------------------------------------
    // Public DTO
    // -------------------------------------------------------------------------

    @Mapping(source = "id",                               target = "productAttributeValueId")
    @Mapping(source = "attributeValue.id",                target = "attributeValueId")
    @Mapping(target = "value", expression = "java(entity.getAttributeValue() != null ? entity.getAttributeValue().getValueAsString() : null)")
    @Mapping(source = "attributeValue.slug", target = "valueSlug")
    ProductAttributeValuePublicResponseDTO toPublicResponseDTO(ProductAttributeValue entity);

    // -------------------------------------------------------------------------
    // Admin DTO (with audit)
    // -------------------------------------------------------------------------

    @Mapping(source = "id",                               target = "productAttributeValueId")
    @Mapping(source = "attributeValue.id",                target = "attributeValueId")
    @Mapping(target = "value", expression = "java(entity.getAttributeValue() != null ? entity.getAttributeValue().getValueAsString() : null)")
    @Mapping(source = "attributeValue.slug", target = "valueSlug")
    @Mapping(target = "audit", source = ".")
    ProductAttributeValueAdminResponseDTO toAdminResponseDTO(ProductAttributeValue entity);

    // Custom mapping method to handle Object to String conversion
    default String map(Object value) {
        return value != null ? value.toString() : null;
    }
}
