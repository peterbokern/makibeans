package com.makibeans.attribute.attribute.mapper;

import com.makibeans.attribute.attribute.dto.AttributeUpdateDTO;
import com.makibeans.attribute.attribute.dto.AttributeAdminResponseDTO;
import com.makibeans.attribute.attribute.dto.AttributePublicResponseDTO;
import com.makibeans.attribute.attribute.model.Attribute;
import com.makibeans.audit.mapper.AuditableMapper;
import org.mapstruct.*;



@Mapper(componentModel = "spring", uses ={AuditableMapper.class})
public interface AttributeMapper {

    /**
     * Converts an Attribute entity to an AttributeTemplateResponseDTO.
     *
     * @param entity the Attribute entity to convert
     * @return the converted AttributeTemplateResponseDTO
     */


    @Mapping(target = "dataType", expression = "java(entity.getDataType().getLabel())")
    @Mapping(target = "inputType", expression = "java(entity.getInputType().getLabel())")
    AttributePublicResponseDTO toPublicResponseDTO(Attribute entity);

    @Mapping(target = "dataType",  expression = "java(entity.getDataType().getLabel())")
    @Mapping(target = "inputType", expression = "java(entity.getInputType().getLabel())")
    @Mapping(target = "audit", source = ".")
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