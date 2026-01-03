package com.makibeans.attribute.attribute.mapper;

import com.makibeans.attribute.attribute.dto.*;
import com.makibeans.attribute.attribute.model.Attribute;
import com.makibeans.attribute.attributevalue.mapper.AttributeValueMapper;
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
    void updateEntityFromDTO(AttributeUpdateDTO updateDTO, @MappingTarget Attribute attribute);

    @AfterMapping @SuppressWarnings("unused")
    default void trimStrings(@MappingTarget Attribute attribute) {

        if (attribute.getDescription() != null) {
            attribute.setDescription(attribute.getDescription().trim());
        }
    }

}