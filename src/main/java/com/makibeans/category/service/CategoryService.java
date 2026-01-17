package com.makibeans.category.service;

import com.makibeans.category.dto.CategoryRequestDTO;
import com.makibeans.category.dto.CategoryUpdateDTO;
import com.makibeans.category.filter.CategoryAdminFilter;
import com.makibeans.category.filter.CategoryPublicFilter;
import com.makibeans.category.model.Category;
import com.makibeans.search.SearchRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface CategoryService  {

    Category getById(Long id);

    @Transactional(readOnly = true)
    Category getByIdIncludingDeleted(Long id);

    @Transactional(readOnly = true)
    List<Category> getCategoryTree();

    Category create(CategoryRequestDTO body) throws BadRequestException;

    Category update(Long id, CategoryUpdateDTO body) throws BadRequestException;

    @Transactional(readOnly = true)
    <F> Page<Category> search(SearchRequest<F> req, Class<F> filterClass);

    @Transactional
    Category makeRoot(Long categoryId);

    @Transactional
    void delete(Long categoryId);

    @Transactional
    Category restore(Long categoryId) throws BadRequestException;

    Category uploadCategoryImage(Long categoryId, MultipartFile image);

    void deleteCategoryImage(Long categoryId);

    byte[] getCategoryImage(Long categoryId);

    @Transactional(readOnly = true)
    Page<Category> searchPublic(SearchRequest<CategoryPublicFilter> req);

    @Transactional(readOnly = true)
    Page<Category> searchAdmin(SearchRequest<CategoryAdminFilter> req);

    Category getBySlugIncludingDeleted(String slug);

    Category getBySlug(String slug);
}