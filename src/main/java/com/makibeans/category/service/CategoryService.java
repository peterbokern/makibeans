package com.makibeans.category.service;

import com.makibeans.category.dto.CategoryRequestDTO;
import com.makibeans.category.dto.CategoryUpdateDTO;
import com.makibeans.category.model.Category;
import com.makibeans.search.SearchRequest;
import com.makibeans.category.filter.CategoryFilter;
import com.makibeans.common.service.CrudService;
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



    Category getById(Long id);

    Page<Category> search(SearchRequest<CategoryFilter> req);

    Category create(CategoryRequestDTO body);

    Category update(Long id, CategoryUpdateDTO body);

    Category uploadCategoryImage(Long categoryId, MultipartFile image);

    void deleteCategoryImage(Long categoryId);

    byte[] getCategoryImage(Long categoryId);
}