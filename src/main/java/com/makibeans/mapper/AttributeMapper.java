package com.makibeans.mapper;

import com.makibeans.dto.attribute.AttributeAdminResponseDTO;
import com.makibeans.dto.attribute.AttributePublicResponseDTO;
import com.makibeans.dto.attribute.AttributeResponseDTO;
import com.makibeans.dto.attribute.AttributeUpdateDTO;
import com.makibeans.dto.categoryattribute.CategoryAttributeUpdateDTO;
import com.makibeans.model.Attribute;
import com.makibeans.model.CategoryAttribute;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Attribute} and its DTO {@link AttributeResponseDTO}.
 */

@Mapper(componentModel = "spring", uses = {AuditableMapper.class})
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

    @Mapping(target = "audit", expression = "java(AuditableMapper.toAuditableInfo(entity))")
    AttributeAdminResponseDTO toAdminResponseDTO(Attribute entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(AttributeUpdateDTO updateDTO, @MappingTarget Attribute attribute);

}