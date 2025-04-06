package com.makibeans.mapper;

import com.makibeans.dto.attributetemplate.AttributeTemplateResponseDTO;
import com.makibeans.model.AttributeTemplate;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link AttributeTemplate} and its DTO {@link AttributeTemplateResponseDTO}.
 */

@Mapper(componentModel = "spring")
public interface AttributeTemplateMapper {

    /**
     * Converts an AttributeTemplate entity to an AttributeTemplateResponseDTO.
     *
     * @param entity the AttributeTemplate entity to convert
     * @return the converted AttributeTemplateResponseDTO
     */

    AttributeTemplateResponseDTO toResponseDTO(AttributeTemplate entity);
}
