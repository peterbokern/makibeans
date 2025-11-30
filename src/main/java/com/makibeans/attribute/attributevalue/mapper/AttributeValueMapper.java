package com.makibeans.attribute.attributevalue.mapper;

import com.makibeans.attribute.attribute.model.AttributeDataType;
import com.makibeans.attribute.attributevalue.dto.*;
import com.makibeans.attribute.attributevalue.model.AttributeValue;
import com.makibeans.audit.mapper.AuditableMapper;
import com.makibeans.common.util.MappingUtils;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AttributeValue} and its DTOs {@link AttributeValueRequestDTO} and {@link AttributeValueResponseDTO}.
 */

@Mapper(componentModel = "spring", uses = {MappingUtils.class, AuditableMapper.class})
public interface AttributeValueMapper {

    /**
     * Converts an AttributeValue entity to an AttributeValueResponseDTO.
     *
     * @param entity the AttributeValue entity to convert
     * @return the converted AttributeValueResponseDTO
     */

    @Mapping(source = "attribute.id", target = "attributeId")
    @Mapping(source = "attribute.name", target = "attributeName")
    @Mapping(target = "value", expression = "java(entity.getValueAsString())")
    @Mapping(target = "dataType", expression = "java(entity.getAttribute().getDataType().getLabel();)")
    @Mapping(target = "inputType", expression = "java(entity.getAttribute().getInputType().getLabel();)")
    @Mapping(target = "slug", source = "slug")
    @Mapping(target = "sortOrder", source = "sortOrder")
    AttributeValuePublicResponseDTO toPublicResponseDTO(AttributeValue entity);

    @Mapping(source = "attribute.id", target = "attributeId")
    @Mapping(source = "attribute.name", target = "attributeName")
    @Mapping(target = "value", expression = "java(entity.getValueAsString())")
    @Mapping(target = "dataType", expression = "java(entity.getAttribute().getDataType().getLabel();)")
    @Mapping(target = "inputType", expression = "java(entity.getAttribute().getInputType().getLabel();)")
    @Mapping(target = "slug", source = "slug")
    @Mapping(target = "sortOrder", source = "sortOrder")
    @Mapping(target = "audit", source= ".")
    AttributeValueAdminResponseDTO toAdminResponseDTO(AttributeValue entity);

    @SuppressWarnings("unused")
    @Mapping(target = "stringValue", ignore = true)
    @Mapping(target = "numericValue", ignore = true)
    @Mapping(target = "booleanValue", ignore = true)
    @Mapping(target = "dateValue", ignore = true)
    @Mapping(target = "dateTimeValue", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "attribute", ignore = true)
    @Mapping(target = "sortOrder", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(AttributeValueUpdateDTO updateDTO, @MappingTarget AttributeValue attributeValue);

    /**
     * Maps an AttributeDataType enum value to its string representation.
     *
     * @param dataType the AttributeDataType enum value
     * @return the string representation of the data type, or null if the input is null
     */
    default String mapDataType(AttributeDataType dataType) {
        return dataType != null ? dataType.name().toLowerCase() : null;
    }

    default String mapInputType(AttributeDataType inputType) {
        return inputType != null ? mapDataType(inputType) : null;
    }
}
