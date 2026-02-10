package com.makibeans.attribute.mapper;

import com.makibeans.attribute.dto.AttributeAdminResponseDTO;
import com.makibeans.attribute.model.Attribute;
import com.makibeans.attribute.dto.AttributeUpdateDTO;
import com.makibeans.audit.mapper.AuditableMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses ={AuditableMapper.class})
public interface AttributeMapper {


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