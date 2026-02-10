package com.makibeans.category.mapper;

import com.makibeans.audit.mapper.AuditableMapper;
import com.makibeans.category.dto.*;
import com.makibeans.category.filter.dto.AttributeFilterDefinitionDTO;
import com.makibeans.category.model.Category;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {AuditableMapper.class})
public interface CategoryMapper {

    /**
     * Converts a Category entity to a CategoryResponseDTO.
     *
     * @param category the Category entity to convert
     * @return the converted CategoryResponseDTO
     */

    @Mapping(source = ".", target = "imageUrl", qualifiedByName = "getImageUrl")
    CategoryTreeResponseDTO toTreeDTO(Category category);

    List<CategoryTreeResponseDTO> toTreeDTOs(List<Category> categories);


    @Mapping(source = "category.id", target = "id")
    //@Mapping(source = "subCategories", target = "subCategories")
    @Mapping(target = "breadcrumbs", expression = "java(buildBreadcrumbs(category))")
    @Mapping(source = "parentCategory", target = "parent")
    @Mapping(source = ".", target = "imageUrl", qualifiedByName = "getImageUrl")
    CategoryPublicResponseDTO toPublicResponseDTO(Category category);

    @Mapping(source = "category.id", target = "id")
    @Mapping(target = "breadcrumbs", expression = "java(buildBreadcrumbs(category))")
    @Mapping(source = "parentCategory", target = "parent")
    @Mapping(source = ".", target = "imageUrl", qualifiedByName = "getImageUrl")
    @Mapping(target  = "audit", source = ".")
    CategoryAdminResponseDTO toAdminResponseDTO(Category category);

    CategoryRefDTO toRefDTO(Category category);


    AttributeFilterDefinitionDTO toFilterDTO(Category category);


    @Named("getImageUrl")
    default String getImageUrl(Category category) {
        return category.getImage() != null
                ? "/categories/" + category.getId() + "/image"
                : null;
    }

    /**
     * Builds a list of breadcrumbs for the given category.
     * The breadcrumbs represent the hierarchy of parent categories.
     *
     * @param category the category for which to register breadcrumbs
     * @return a list of BreadCrumbDTOs representing the breadcrumb trail
     */

    default List<CategoryRefDTO> buildBreadcrumbs(Category category) {
        List<CategoryRefDTO> breadcrumbs = new ArrayList<>();
        Category current = category;

        while (current != null) {
            breadcrumbs.addFirst(new CategoryRefDTO(
                    current.getId(),
                    current.getName(),
                    current.getSlug()
            ));
            current = current.getParentCategory();
        }

        return breadcrumbs;
    }


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "parentCategory", ignore = true)
    void updateEntityFromDTO(CategoryUpdateDTO updateDTO, @MappingTarget Category category);


}
