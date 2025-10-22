package com.makibeans.mapper;

import com.makibeans.dto.attribute.AttributeUpdateDTO;
import com.makibeans.dto.productvariant.ProductVariantResponseDTO;
import com.makibeans.dto.productvariant.ProductVariantUpdateDTO;
import com.makibeans.model.Attribute;
import com.makibeans.model.ProductVariant;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductVariant} and its DTO {@link ProductVariantResponseDTO}.
 */

@Mapper(componentModel = "spring", uses = {AttributeValueMapper.class, SizeMapper.class})
public interface ProductVariantMapper {

    /**
     * Converts a ProductVariant entity to a ProductVariantResponseDTO.
     *
     * @param entity the ProductVariant entity to convert
     * @return the converted ProductVariantResponseDTO
     */

    @Mapping(source = "size.id", target = "sizeId")
    @Mapping(source = "size.name", target = "sizeName")
    ProductVariantResponseDTO toResponseDTO(ProductVariant entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(ProductVariantUpdateDTO updateDTO, @MappingTarget ProductVariant productVariant);
}
