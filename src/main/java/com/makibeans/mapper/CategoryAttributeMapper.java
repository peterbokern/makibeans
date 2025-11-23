package com.makibeans.mapper;

import com.makibeans.dto.categoryattribute.CategoryAttributeAdminResponseDTO;
import com.makibeans.dto.categoryattribute.CategoryAttributePublicResponseDTO;
import com.makibeans.dto.categoryattribute.CategoryAttributeResponseDTO;
import com.makibeans.dto.categoryattribute.CategoryAttributeUpdateDTO;
import com.makibeans.model.CategoryAttribute;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, uses = AuditableMapper.class)
public interface CategoryAttributeMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "attribute.id", target = "attributeId")
    @Mapping(source = "attribute.name", target = "attributeName")
    @Mapping (source = "required", target = "required")
    CategoryAttributeResponseDTO toResponseDTO(CategoryAttribute categoryAttribute);

    // --- Public DTO mapping ---

    @Mapping(source = "id",              target = "id")
    @Mapping(source = "category.id",     target = "categoryId")
    @Mapping(source = "category.name",   target = "categoryName")
    @Mapping(source = "attribute.id",    target = "attributeId")
    @Mapping(source = "attribute.name",  target = "attributeName")
    @Mapping(source = "required",        target = "required")
    CategoryAttributePublicResponseDTO toPublicResponseDTO(CategoryAttribute categoryAttribute);

    // --- Admin DTO mapping ---

    @Mapping(source = "id",              target = "id")
    @Mapping(source = "category.id",     target = "categoryId")
    @Mapping(source = "category.name",   target = "categoryName")
    @Mapping(source = "attribute.id",    target = "attributeId")
    @Mapping(source = "attribute.name",  target = "attributeName")
    @Mapping(source = "required",        target = "required")
    @Mapping(target = "audit", expression = "java(AuditableMapper.toAuditableInfo(categoryAttribute))")
    CategoryAttributeAdminResponseDTO toAdminResponseDTO(CategoryAttribute categoryAttribute);


    //TODO add update method to each entity mapper
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(CategoryAttributeUpdateDTO updateDTO, @MappingTarget CategoryAttribute categoryAttribute);
}
