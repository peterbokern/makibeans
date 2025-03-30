package com.makibeans.mapper;

import com.makibeans.dto.AttributeValueRequestDTO;
import com.makibeans.dto.AttributeValueResponseDTO;
import com.makibeans.model.AttributeValue;
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

    AttributeValueResponseDTO toResponseDTO(AttributeValue entity);



}
