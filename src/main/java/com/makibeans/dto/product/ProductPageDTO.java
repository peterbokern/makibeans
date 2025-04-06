package com.makibeans.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for ProductPage.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPageDTO {
    private List<ProductResponseDTO> content;
    private int page;
    private int totalPages;
    private int size;
    private Long totalElements;
}
