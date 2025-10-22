package com.makibeans.mapper;

import com.makibeans.dto.productvariant.ProductVariantUpdateDTO;
import com.makibeans.dto.size.SizeResponseDTO;
import com.makibeans.dto.size.SizeUpdateDTO;
import com.makibeans.model.ProductVariant;
import com.makibeans.model.Size;
import com.makibeans.util.MappingUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper for the entity {@link Size} and its DTO {@link SizeResponseDTO}.
 */

@Mapper(componentModel = "spring", uses = MappingUtils.class)
public interface SizeMapper {

    /**
     * Converts a Size entity to a SizeResponseDTO.
     *
     * @param entity the Size entity to convert
     * @return the converted SizeResponseDTO
     */

    SizeResponseDTO toResponseDTO(Size entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(SizeUpdateDTO updateDTO, @MappingTarget Size size);
}