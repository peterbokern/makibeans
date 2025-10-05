package com.makibeans.mapper;

import com.makibeans.dto.categoryattribute.CategoryAttributeResponseDTO;
import com.makibeans.dto.categoryattribute.CategoryAttributeUpdateDTO;
import com.makibeans.model.CategoryAttribute;
import org.mapstruct.*;


@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CategoryAttributeMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "attribute.id", target = "attributeId")
    @Mapping(source = "attribute.name", target = "attributeName")
    @Mapping (source = "required", target = "required")
    CategoryAttributeResponseDTO toResponseDTO(com.makibeans.model.CategoryAttribute categoryAttribute);

    //TODO add update method to each entity mapper
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(CategoryAttributeUpdateDTO updateDTO, @MappingTarget CategoryAttribute categoryAttribute);
}
