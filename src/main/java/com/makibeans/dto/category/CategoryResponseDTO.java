package com.makibeans.dto.category;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.makibeans.dto.base.AuditableResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for Category responses.
 */

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "name", "description", "imageUrl", "parentCategoryId", "subCategories", "breadCrumbs", "createdBy", "createdAt", "updatedBy", "updatedAt"})
public class CategoryResponseDTO extends AuditableResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Long parentCategoryId;

    private List<CategoryResponseDTO> subCategories;
    private List<BreadCrumbDTO> breadCrumbs;
}
