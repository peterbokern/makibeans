package com.makibeans.category.service;

import com.makibeans.category.filter.dto.CategoryFilterDefinitionsResponseDTO;
import org.springframework.transaction.annotation.Transactional;

public interface CategoryFilterService {

    @Transactional(readOnly = true)
    CategoryFilterDefinitionsResponseDTO getCategoryFilterDefinitions(Long categoryId);
}
