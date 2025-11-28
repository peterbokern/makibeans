package com.makibeans.attribute.attribute.mapper;

import com.makibeans.attribute.attribute.dto.AttributeResponseDTO;
import com.makibeans.attribute.attribute.dto.AttributeUpdateDTO;
import com.makibeans.attribute.attribute.dto.AttributeAdminResponseDTO;
import com.makibeans.attribute.attribute.dto.AttributePublicResponseDTO;
import com.makibeans.attribute.attribute.model.Attribute;
import com.makibeans.attribute.attribute.model.AttributeDataType;
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

    @Mapping(target = "dataType", expression = "java(entity.getDataType().getLabel())")
    @Mapping(target = "inputType", expression = "java(entity.getInputType().getLabel())")
    AttributePublicResponseDTO toPublicResponseDTO(Attribute entity);

    @Mapping(target = "dataType",  expression = "java(entity.getDataType().getLabel())")
    @Mapping(target = "inputType", expression = "java(entity.getInputType().getLabel())")
    @Mapping(target = "audit", expression = "java(com.makibeans.audit.mapper.AuditableMapper.toAuditableInfo(entity))")
    AttributeAdminResponseDTO toAdminResponseDTO(Attribute entity);

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(AttributeUpdateDTO updateDTO, @MappingTarget Attribute attribute);

 /*   @SuppressWarnings("unused")
    @Named("enumToLowerCase")
    static String enumToLowerCase(Enum dataType) {
        return dataType != null ? dataType.name().toLowerCase() : null;
    }*/
}