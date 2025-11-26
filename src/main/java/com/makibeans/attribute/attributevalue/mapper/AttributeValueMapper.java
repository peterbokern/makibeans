package com.makibeans.attribute.attributevalue.mapper;

import com.makibeans.attribute.attributevalue.dto.*;
import com.makibeans.attribute.attributevalue.model.AttributeValue;
import com.makibeans.util.MappingUtils;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AttributeValue} and its DTOs {@link AttributeValueRequestDTO} and {@link AttributeValueResponseDTO}.
 */

@Mapper(componentModel = "spring", uses = MappingUtils.class)
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
    @Mapping(target = "dataType", expression = "java(mapDataType(entity.getAttribute().getDataType()))")
    AttributeValuePublicResponseDTO toPublicResponseDTO(AttributeValue entity);

    @Mapping(source = "attribute.id", target = "attributeId")
    @Mapping(source = "attribute.name", target = "attributeName")
    @Mapping(target = "value", expression = "java(entity.getValueAsString())")
    @Mapping(target = "dataType", expression = "java(mapDataType(entity.getAttribute().getDataType()))")
    @Mapping(target = "audit", expression = "java(com.makibeans.audit.mapper.AuditableMapper.toAuditableInfo(entity))")
    AttributeValueAdminResponseDTO toAdminResponseDTO(AttributeValue entity);

    @SuppressWarnings("unused")
    default String mapDataType(Enum<?> dataType) {
        return dataType != null ? dataType.name().toLowerCase() : null;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(AttributeValueUpdateDTO updateDTO, @MappingTarget AttributeValue attributeValue);
}
