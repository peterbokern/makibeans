package com.makibeans.categoryattribute.mapper;

import com.makibeans.categoryattribute.dto.CategoryAttributeAdminResponseDTO;
import com.makibeans.categoryattribute.dto.CategoryAttributeResponseDTO;
import com.makibeans.categoryattribute.dto.CategoryAttributeUpdateDTO;
import com.makibeans.categoryattribute.model.CategoryAttribute;
import com.makibeans.audit.mapper.AuditableMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AuditableMapper.class})
public interface CategoryAttributeMapper {

    @Mapping(source = "id",              target = "categoryAttributeId")
    @Mapping(source = "attribute.id",    target = "attributeId")
    @Mapping(source = "attribute.name",  target = "attributeName")
    @Mapping(source = "required",        target = "required")
    @Mapping(source = "sortOrder",      target = "sortOrder")
    CategoryAttributeResponseDTO toResponseDTO(CategoryAttribute categoryAttribute);

    @Mapping(source = "id",              target = "categoryAttributeId")
    @Mapping(source = "attribute.id",    target = "attributeId")
    @Mapping(source = "attribute.name",  target = "attributeName")
    @Mapping(source = "required",        target = "required")
    @Mapping(source = "sortOrder",      target = "sortOrder")
    @Mapping(target = "audit", source = ".")
    CategoryAttributeAdminResponseDTO toAdminResponseDTO(CategoryAttribute categoryAttribute);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(CategoryAttributeUpdateDTO updateDTO, @MappingTarget CategoryAttribute categoryAttribute);
}
