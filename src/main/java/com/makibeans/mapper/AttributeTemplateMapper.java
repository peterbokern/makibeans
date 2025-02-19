package com.makibeans.mapper;

import com.makibeans.dto.AttributeTemplateRequestDTO;
import com.makibeans.dto.AttributeTemplateResponseDTO;
import com.makibeans.model.AttributeTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AttributeTemplateMapper {
    AttributeTemplateMapper INSTANCE = Mappers.getMapper(AttributeTemplateMapper.class);
    AttributeTemplate toEntity(AttributeTemplateRequestDTO dto);
    AttributeTemplateResponseDTO toResponseDTO(AttributeTemplate entity);
}
