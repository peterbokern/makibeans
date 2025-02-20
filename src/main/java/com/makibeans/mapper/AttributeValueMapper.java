package com.makibeans.mapper;
import com.makibeans.dto.AttributeValueResponseDTO;
import com.makibeans.model.AttributeValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttributeValueMapper {

    @Mapping(source = "attributeTemplate.id", target = "templateId")
    AttributeValueResponseDTO toResponseDTO(AttributeValue entity);
}
