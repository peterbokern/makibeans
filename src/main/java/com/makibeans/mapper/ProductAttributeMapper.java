package com.makibeans.mapper;

import com.makibeans.dto.attribute.AttributeUpdateDTO;
import com.makibeans.dto.productattribute.ProductAttributeResponseDTO;
import com.makibeans.dto.attributevalue.AttributeValueSimpleResponseDTO;
import com.makibeans.model.Attribute;
import com.makibeans.model.ProductAttribute;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for the entity {@link ProductAttribute} and its DTO {@link ProductAttributeResponseDTO}.
 */

@Mapper(componentModel = "spring")
public interface ProductAttributeMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "attribute.id", target = "attributeId")
    @Mapping(source = "attribute.name", target = "attributeName")
    @Mapping(source = ".", target = "values", qualifiedByName = "mapAttributeValues")
    ProductAttributeResponseDTO toResponseDTO(ProductAttribute entity);

    @Named("mapAttributeValues")
    default Set<AttributeValueSimpleResponseDTO> mapAttributeValues(ProductAttribute productAttribute) {
        return productAttribute.getProductAttributeValues()
                .stream()
                .map(productattributeValue -> new AttributeValueSimpleResponseDTO(productattributeValue.getAttributeValue().getId(), productattributeValue.getAttributeValue().getValue()))
                .collect(Collectors.toSet());
    }

}
