package com.makibeans.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for Category responses.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Long parentCategoryId;

    private List<CategoryResponseDTO> subCategories;
    private List<BreadCrumbDTO> breadCrumbs;
}
