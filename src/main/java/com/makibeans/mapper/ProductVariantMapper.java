package com.makibeans.mapper;

import com.makibeans.dto.ProductVariantResponseDTO;
import com.makibeans.model.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AttributeValueMapper.class, SizeMapper.class})
public interface ProductVariantMapper {

    @Mapping(source = "size.id", target = "sizeId")
    @Mapping(source = "size.name", target = "sizeName")
    ProductVariantResponseDTO toResponseDTO(ProductVariant entity);
}
