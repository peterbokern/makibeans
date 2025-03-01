package com.makibeans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
