package com.makibeans.mapper;

import com.makibeans.dto.category.BreadCrumbDTO;
import com.makibeans.dto.category.CategoryRequestDTO;
import com.makibeans.dto.category.CategoryResponseDTO;
import com.makibeans.model.Category;
import com.makibeans.util.MappingUtils;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for the entity {@link Category} and its DTOs {@link CategoryRequestDTO} and {@link CategoryResponseDTO}.
 */

@Mapper(componentModel = "spring", uses = MappingUtils.class)
public interface CategoryMapper {

    /**
     * Converts a Category entity to a CategoryResponseDTO.
     *
     * @param category the Category entity to convert
     * @return the converted CategoryResponseDTO
     */

    @Mapping(source = "category.id", target = "id")
    @Mapping(source = "subCategories", target = "subCategories")
    @Mapping(target = "breadCrumbs", ignore = true)
    @Mapping(source = "parentCategory.id", target = "parentCategoryId")
    @Mapping(source = ".", target = "imageUrl", qualifiedByName = "getImageUrl")
    CategoryResponseDTO toResponseDTO(Category category);

    /**
     * Returns the image URL of the given product.
     *
     * @param category the category to get the image URL from
     * @return the image URL of the given product
     */

    @Named("getImageUrl")
    default String getImageUrl(Category category) {
        return category.getImage() != null
                ? "/categories/" + category.getId() + "/image"
                : "null";
    }

    /**
     * Converts a list of Category entities to a list of CategoryResponseDTOs.
     *
     * @param categories the list of Category entities to convert
     * @return the list of converted CategoryResponseDTOs
     */

    List<CategoryResponseDTO> toResponseDTOList(List<Category> categories);

    /**
     * Builds a list of breadcrumbs for the given category.
     * The breadcrumbs represent the hierarchy of parent categories.
     *
     * @param category the category for which to build breadcrumbs
     */

    @AfterMapping
    default void setBreadcrumbs(@MappingTarget CategoryResponseDTO dto, Category category) {
        dto.setBreadCrumbs(buildBreadcrumbs(category));
    }

    /**
     * Builds a list of breadcrumbs for the given category.
     * The breadcrumbs represent the hierarchy of parent categories.
     *
     * @param category the category for which to build breadcrumbs
     * @return a list of BreadCrumbDTOs representing the breadcrumb trail
     */

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
