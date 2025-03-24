package com.makibeans.mapper;

import com.makibeans.dto.AttributeTemplateRequestDTO;
import com.makibeans.dto.AttributeTemplateResponseDTO;
import com.makibeans.model.AttributeTemplate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttributeTemplateMapper {
    AttributeTemplateResponseDTO toResponseDTO(AttributeTemplate entity);
}
