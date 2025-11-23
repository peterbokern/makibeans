package com.makibeans.categoryattribute.mapper;

import com.makibeans.categoryattribute.dto.CategoryAttributeAdminResponseDTO;
import com.makibeans.categoryattribute.dto.CategoryAttributePublicResponseDTO;
import com.makibeans.categoryattribute.dto.CategoryAttributeResponseDTO;
import com.makibeans.categoryattribute.dto.CategoryAttributeUpdateDTO;
import com.makibeans.categoryattribute.model.CategoryAttribute;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
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
    @Mapping(target = "audit", expression = "java(com.makibeans.audit.mapper.AuditableMapper.toAuditableInfo(categoryAttribute))")
    CategoryAttributeAdminResponseDTO toAdminResponseDTO(CategoryAttribute categoryAttribute);


    //TODO add update method to each entity mapper
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(CategoryAttributeUpdateDTO updateDTO, @MappingTarget CategoryAttribute categoryAttribute);
}
