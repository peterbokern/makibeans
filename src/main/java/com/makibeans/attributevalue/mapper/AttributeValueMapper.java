package com.makibeans.attributevalue.mapper;

import com.makibeans.attributevalue.dto.*;
import com.makibeans.attributevalue.model.AttributeValue;
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
    AttributeValueResponseDTO toResponseDTO(AttributeValue entity);

    @Mapping(source = "attribute.id", target = "attributeId")
    @Mapping(source = "attribute.name", target = "attributeName")
    AttributeValuePublicResponseDTO toPublicResponseDTO(AttributeValue entity);

    @Mapping(source = "attribute.id", target = "attributeId")
    @Mapping(source = "attribute.name", target = "attributeName")
    AttributeValueAdminResponseDTO toAdminResponseDTO(AttributeValue entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(AttributeValueUpdateDTO updateDTO, @MappingTarget AttributeValue attributeValue);



}
