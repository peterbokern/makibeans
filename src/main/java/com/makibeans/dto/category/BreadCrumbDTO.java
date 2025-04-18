package com.makibeans.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for BreadCrumb.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BreadCrumbDTO {
    private Long id;
    private String name;

}
