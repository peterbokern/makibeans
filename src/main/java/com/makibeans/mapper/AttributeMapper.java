package com.makibeans.mapper;

import com.makibeans.dto.attribute.AttributeResponseDTO;
import com.makibeans.model.Attribute;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Attribute} and its DTO {@link AttributeResponseDTO}.
 */

@Mapper(componentModel = "spring")
public interface AttributeMapper {

    /**
     * Converts an Attribute entity to an AttributeTemplateResponseDTO.
     *
     * @param entity the Attribute entity to convert
     * @return the converted AttributeTemplateResponseDTO
     */

    AttributeResponseDTO toResponseDTO(Attribute entity);
}
