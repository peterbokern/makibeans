package com.makibeans.mapper;

import com.makibeans.dto.ProductAttributeResponseDTO;
import com.makibeans.dto.AttributeValueSimpleResponseDTO;
import com.makibeans.model.ProductAttribute;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper for the entity {@link ProductAttribute} and its DTO {@link ProductAttributeResponseDTO}.
 */

@Mapper(componentModel = "spring")
public interface ProductAttributeMapper {

    @Mapping(source = "attributeTemplate.id", target = "attributeTemplateId")
    @Mapping(source = "attributeTemplate.name", target = "attributeTemplateName")
    @Mapping(source = ".", target = "values", qualifiedByName = "mapAttributeValues")
    ProductAttributeResponseDTO toResponseDTO(ProductAttribute entity);

    @Named("mapAttributeValues")
    default List<AttributeValueSimpleResponseDTO> mapAttributeValues(ProductAttribute productAttribute) {
        return productAttribute.getAttributeValues()
                .stream()
                .map(attributeValue -> new AttributeValueSimpleResponseDTO(attributeValue.getId(), attributeValue.getValue()))
                .toList();
    }
}
