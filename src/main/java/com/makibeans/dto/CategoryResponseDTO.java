package com.makibeans.dto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Long parentCategoryId;

    @JsonManagedReference
    private List<CategoryResponseDTO> subCategories;

    private List<BreadCrumbDTO> breadCrumbs;
}
