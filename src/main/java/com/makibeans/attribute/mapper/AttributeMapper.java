package com.makibeans.attribute.mapper;

import com.makibeans.attribute.dto.AttributeAdminResponseDTO;
import com.makibeans.attribute.dto.AttributePublicResponseDTO;
import com.makibeans.attribute.dto.AttributeResponseDTO;
import com.makibeans.attribute.dto.AttributeUpdateDTO;
import com.makibeans.attribute.model.Attribute;
import org.mapstruct.*;

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

    //remove
    AttributeResponseDTO toResponseDTO(Attribute entity);

    AttributePublicResponseDTO toPublicResponseDTO(Attribute entity);

    @Mapping(target = "audit", expression = "java(com.makibeans.audit.mapper.AuditableMapper.toAuditableInfo(entity))")
    AttributeAdminResponseDTO toAdminResponseDTO(Attribute entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(AttributeUpdateDTO updateDTO, @MappingTarget Attribute attribute);

}