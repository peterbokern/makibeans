package com.makibeans.mapper;

import com.makibeans.dto.ProductAttributeResponseDTO;
import com.makibeans.model.ProductAttribute;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductAttribute} and its DTO {@link ProductAttributeResponseDTO}.
 */

@Mapper(componentModel = "spring", uses = AttributeValueMapper.class)
public interface ProductAttributeMapper {

    /**
     * Converts a ProductAttribute entity to a ProductAttributeResponseDTO.
     *
     * @param entity the ProductAttribute entity to convert
     * @return the converted ProductAttributeResponseDTO
     */

    @Named("toSimpleResponseDTO")
    @Mapping(source = "attributeTemplate.id", target = "attributeTemplateId")
    @Mapping(source = "attributeTemplate.name", target = "attributeTemplateName")
    @Mapping(source = "attributeValues", target = "attributeValues")
    ProductAttributeResponseDTO toResponseDTO(ProductAttribute entity);
}
