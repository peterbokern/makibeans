package com.makibeans.attribute.productattribute.mapper;

import com.makibeans.attribute.productattribute.dto.ProductAttributeAdminResponseDTO;
import com.makibeans.attribute.productattribute.dto.ProductAttributePublicResponseDTO;
import com.makibeans.attribute.productattribute.model.ProductAttribute;
import com.makibeans.audit.mapper.AuditableMapper;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {AuditableMapper.class})
public interface ProductAttributeMapper {


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
    @Mapping(target = "audit", source = ".")
    ProductAttributeAdminResponseDTO toAdminResponseDTO(ProductAttribute productAttribute);

}
