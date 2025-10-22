package com.makibeans.service.service;

import com.makibeans.dto.categoryattribute.CategoryAttributeRequestDTO;
import com.makibeans.dto.categoryattribute.CategoryAttributeResponseDTO;
import com.makibeans.dto.categoryattribute.CategoryAttributeUpdateDTO;
import com.makibeans.model.CategoryAttribute;
import com.makibeans.search.filters.CategoryAttributeFilter;
import com.makibeans.search.SearchRequest;
import org.springframework.data.domain.Page;

public interface CategoryAttributeService extends CrudService<CategoryAttribute, Long>
{
    CategoryAttributeResponseDTO geById(Long id);
    Page<CategoryAttributeResponseDTO> search(SearchRequest<CategoryAttributeFilter> req);
    CategoryAttributeResponseDTO create(CategoryAttributeRequestDTO body);
    CategoryAttributeResponseDTO update(Long id, CategoryAttributeUpdateDTO body);
    Boolean existByCategoryId(Long categoryId);
}
