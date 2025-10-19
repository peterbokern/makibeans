package com.makibeans.service;

import com.makibeans.dto.categoryattribute.CategoryAttributeRequestDTO;
import com.makibeans.dto.categoryattribute.CategoryAttributeResponseDTO;
import com.makibeans.dto.categoryattribute.CategoryAttributeUpdateDTO;
import com.makibeans.search.filters.CategoryAttributeFilter;
import com.makibeans.search.SearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryAttributeService {

    //write
    CategoryAttributeResponseDTO create(CategoryAttributeRequestDTO requestDTO);
    CategoryAttributeResponseDTO update(Long id, CategoryAttributeUpdateDTO updateDTO);
    void delete(Long id);

    //read
    CategoryAttributeResponseDTO getById(Long id);
    List<CategoryAttributeResponseDTO> getByCategory(Long categoryId);
    List<CategoryAttributeResponseDTO> getByAttribute(Long attributeId);
    List<CategoryAttributeResponseDTO> getAll();

    @Transactional(readOnly = true)
    Page<CategoryAttributeResponseDTO> search(SearchRequest<CategoryAttributeFilter> req);
}
