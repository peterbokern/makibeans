package com.makibeans.mapper;

import com.makibeans.dto.AttributeValueRequestDTO;
import com.makibeans.dto.AttributeValueResponseDTO;
import com.makibeans.model.AttributeValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AttributeValueMapper {
    AttributeValueMapper INSTANCE = Mappers.getMapper(AttributeValueMapper.class);

    @Mapping(target = "value", expression = "java(normalize(dto.getValue()))")
    AttributeValue toEntity(AttributeValueRequestDTO dto);

    AttributeValueResponseDTO toResponseDTO(AttributeValue entity);

    @Mapping(target = "value", expression = "java(normalize(dto.getValue()))")
    void updateAttributeValueFromDto(AttributeValueRequestDTO dto, @MappingTarget AttributeValue entity);

    default String normalize(String value) {
        return (value == null) ? null : value.trim().toLowerCase();
    }
}
