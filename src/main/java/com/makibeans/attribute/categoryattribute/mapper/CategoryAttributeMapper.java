package com.makibeans.attribute.categoryattribute.mapper;

import com.makibeans.attribute.categoryattribute.dto.CategoryAttributeUpdateDTO;
import com.makibeans.attribute.categoryattribute.dto.CategoryAttributeAdminResponseDTO;
import com.makibeans.attribute.categoryattribute.dto.CategoryAttributePublicResponseDTO;
import com.makibeans.attribute.categoryattribute.model.CategoryAttribute;
import com.makibeans.audit.mapper.AuditableMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AuditableMapper.class})
public interface CategoryAttributeMapper {

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
    @Mapping(target = "audit", source = ".")
    CategoryAttributeAdminResponseDTO toAdminResponseDTO(CategoryAttribute categoryAttribute);


    //TODO add update method to each entity mapper
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(CategoryAttributeUpdateDTO updateDTO, @MappingTarget CategoryAttribute categoryAttribute);
}
