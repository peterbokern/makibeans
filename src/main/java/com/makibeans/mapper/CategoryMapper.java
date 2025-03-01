package com.makibeans.mapper;

import com.makibeans.dto.BreadCrumbDTO;
import com.makibeans.dto.CategoryRequestDTO;
import com.makibeans.dto.CategoryResponseDTO;
import com.makibeans.model.Category;
import com.makibeans.util.MappingUtils;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = MappingUtils.class)
public interface CategoryMapper {

    @Mapping(source = "name", target = "name", qualifiedByName = "normalizeValue")
    @Mapping(source = "description", target = "description", qualifiedByName = "normalizeValue")
    @Mapping(target = "parentCategory", ignore = true) //ignore mapping parentCategory to prevent persistence exception
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryRequestDTO requestDTO);

    @Mapping(source = "category.id", target = "id")
    @Mapping(source = "subCategories", target = "subCategories")
    @Mapping(target = "breadCrumbs", ignore = true)
    @Mapping(source = "parentCategory.id", target = "parentCategoryId")
    CategoryResponseDTO toResponseDTO(Category category);

    List<CategoryResponseDTO> toResponseDTOList(List<Category> categories);

    // Manually map breadcrumbs
    @AfterMapping
    default void setBreadcrumbs(@MappingTarget CategoryResponseDTO dto, Category category) {
        dto.setBreadCrumbs(buildBreadcrumbs(category));
    }

    // Build breadcrumb trail from parent categories
    default List<BreadCrumbDTO> buildBreadcrumbs(Category category) {
        List<BreadCrumbDTO> breadcrumbs = new ArrayList<>();
        Category current = category.getParentCategory();

        while (current != null) {
            breadcrumbs.add(0, new BreadCrumbDTO(current.getId(), current.getName())); // Add at index 0 for correct order
            current = current.getParentCategory();
        }

        return breadcrumbs;
    }
}
