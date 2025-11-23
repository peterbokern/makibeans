package com.makibeans.service.service;

import com.makibeans.dto.category.CategoryRequestDTO;
import com.makibeans.dto.category.CategoryResponseDTO;
import com.makibeans.dto.category.CategoryUpdateDTO;
import com.makibeans.model.Category;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.CategoryFilter;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

/**
 * Application service for {@link Category}.
 * <p>
 * Minimal interface:
 * <ul>
 *   <li>Generic CRUD defaults via {@link CrudService}</li>
 *   <li>DTO-based operations for create/update/get</li>
 *   <li>Typed, pageable search over Categories</li>
 * </ul>
 */
public interface CategoryService extends CrudService<Category, Long> {


    Page<CategoryResponseDTO> search(SearchRequest<CategoryFilter> req);

    /** Return DTO for a single Category. */
    CategoryResponseDTO getById(Long id);

    CategoryResponseDTO create(CategoryRequestDTO body);

    CategoryResponseDTO update(Long id, CategoryUpdateDTO body);

    CategoryResponseDTO uploadCategoryImage(Long categoryId, MultipartFile image);

    void deleteCategoryImage(Long categoryId);

    byte[] getCategoryImage(Long categoryId);
}