
package com.makibeans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryUpdateDTO {
    private String name;
    private String description;
    private String imageUrl;
    private Long parentCategoryId;
}
