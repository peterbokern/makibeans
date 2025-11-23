package com.makibeans.mapper;

import com.makibeans.dto.productattributevalue.ProductAttributeValueResponseDTO;
import com.makibeans.model.ProductAttributeValue;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductAttributeValueMapper {

    ProductAttributeValueResponseDTO toResponseDTO(ProductAttributeValue entity);
}
