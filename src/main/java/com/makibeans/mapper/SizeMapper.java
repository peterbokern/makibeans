package com.makibeans.mapper;

import com.makibeans.dto.SizeRequestDTO;
import com.makibeans.dto.SizeResponseDTO;
import com.makibeans.model.Size;
import com.makibeans.util.MappingUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MappingUtils.class)
public interface SizeMapper {

    SizeResponseDTO toResponseDTO(Size entity);

    @Mapping(source = "name", target = "name", qualifiedByName = "normalizeValue")
    Size toEntity(SizeRequestDTO dto);
}