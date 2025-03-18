package com.makibeans.mapper;

import com.makibeans.dto.ProductVariantRequestDTO;
import com.makibeans.dto.ProductVariantResponseDTO;
import com.makibeans.model.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AttributeValueMapper.class, SizeMapper.class})
public interface ProductVariantMapper {

    ProductVariant toEntity(ProductVariantRequestDTO dto);

    @Mapping(source = "size", target = "size")
    ProductVariantResponseDTO toResponseDTO(ProductVariant entity);
}
