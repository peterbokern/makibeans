package com.makibeans.mapper;

import com.makibeans.dto.ProductAttributeResponseDTO;
import com.makibeans.model.ProductAttribute;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = AttributeValueMapper.class)
public interface ProductAttributeMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "attributeTemplate.id", target = "attributeTemplateId")
    @Mapping(source = "attributeTemplate.name", target = "attributeTemplateName")
    @Mapping(source = "attributeValues", target = "attributeValues")
    ProductAttributeResponseDTO toResponseDTO(ProductAttribute entity);
}
