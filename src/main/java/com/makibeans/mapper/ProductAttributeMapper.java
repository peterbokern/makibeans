package com.makibeans.mapper;

import com.makibeans.dto.productattribute.ProductAttributeResponseDTO;
import com.makibeans.dto.attributevalue.AttributeValueSimpleResponseDTO;
import com.makibeans.model.ProductAttribute;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper for the entity {@link ProductAttribute} and its DTO {@link ProductAttributeResponseDTO}.
 */

@Mapper(componentModel = "spring")
public interface ProductAttributeMapper {

    @Mapping(source = "attribute.id", target = "attributeId")
    @Mapping(source = "attribute.name", target = "attributeName")
    @Mapping(source = ".", target = "values", qualifiedByName = "mapAttributeValues")
    ProductAttributeResponseDTO toResponseDTO(ProductAttribute entity);

    @Named("mapAttributeValues")
    default List<AttributeValueSimpleResponseDTO> mapAttributeValues(ProductAttribute productAttribute) {
        return productAttribute.getProductAttributeValueLinks()
                .stream()
                .map(productattributeValue -> new AttributeValueSimpleResponseDTO(productattributeValue.getAttributeValue().getId(), productattributeValue.getAttributeValue().getValue()))
                .toList();
    }
}
