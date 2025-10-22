package com.makibeans.mapper;

import com.makibeans.dto.attribute.AttributeResponseDTO;
import com.makibeans.dto.attribute.AttributeUpdateDTO;
import com.makibeans.dto.categoryattribute.CategoryAttributeUpdateDTO;
import com.makibeans.model.Attribute;
import com.makibeans.model.CategoryAttribute;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

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

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(AttributeUpdateDTO updateDTO, @MappingTarget Attribute attribute);
}
