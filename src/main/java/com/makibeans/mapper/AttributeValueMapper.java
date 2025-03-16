package com.makibeans.mapper;

import com.makibeans.dto.AttributeValueRequestDTO;
import com.makibeans.dto.AttributeValueResponseDTO;
import com.makibeans.model.AttributeValue;
import com.makibeans.util.MappingUtils;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = MappingUtils.class)
public interface AttributeValueMapper {

 /*   @Mapping(source = "attributeTemplate.id", target = "templateId")
    @Mapping(source = "attributeTemplate.name", target = "attributeName")*/
    AttributeValueResponseDTO toResponseDTO(AttributeValue entity);

    @Mapping(source = "value", target = "value", qualifiedByName = "normalizeValue")
    @Mapping(target = "attributeTemplate", ignore = true)
    AttributeValue toEntity(AttributeValueRequestDTO dto);

}
