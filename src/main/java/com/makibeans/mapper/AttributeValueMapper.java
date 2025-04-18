package com.makibeans.mapper;

import com.makibeans.dto.attributevalue.AttributeValueResponseDTO;
import com.makibeans.dto.attributevalue.AttributeValueRequestDTO;
import com.makibeans.model.AttributeValue;
import com.makibeans.util.MappingUtils;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AttributeValue} and its DTOs {@link AttributeValueRequestDTO} and {@link AttributeValueResponseDTO}.
 */

@Mapper(componentModel = "spring", uses = MappingUtils.class)
public interface AttributeValueMapper {

    /**
     * Converts an AttributeValue entity to an AttributeValueResponseDTO.
     *
     * @param entity the AttributeValue entity to convert
     * @return the converted AttributeValueResponseDTO
     */

    @Mapping(source = "attributeTemplate.id", target = "attributeTemplateId")
    @Mapping(source = "attributeTemplate.name", target = "attributeTemplateName")
    AttributeValueResponseDTO toResponseDTO(AttributeValue entity);
}
