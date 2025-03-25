package com.makibeans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPageDTO {
    private List<ProductResponseDTO> content;
    private int page;
    private int size;
    private Long totalElements;
    private int totalPages;
}
