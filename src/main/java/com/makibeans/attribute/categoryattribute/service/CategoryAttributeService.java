// language: java
package com.makibeans.attribute.categoryattribute.service;

import com.makibeans.attribute.categoryattribute.dto.CategoryAttributeRequestDTO;
import com.makibeans.attribute.categoryattribute.dto.CategoryAttributeUpdateDTO;
import com.makibeans.attribute.categoryattribute.dto.CategoryAttributeUsageDTO;
import com.makibeans.attribute.categoryattribute.filter.CategoryAttributeAdminFilter;
import com.makibeans.attribute.categoryattribute.filter.CategoryAttributePublicFilter;
import com.makibeans.attribute.categoryattribute.model.CategoryAttribute;
import com.makibeans.category.model.Category;
import com.makibeans.search.SearchRequest;
import com.makibeans.attribute.categoryattribute.filter.CategoryAttributeFilter;
import com.makibeans.common.service.CrudService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

/**
 * Domain API for CategoryAttribute links.
 * Services now return entities; controllers are responsible for mapping to DTOs.
 */
public interface CategoryAttributeService  {

    CategoryAttribute getById(Long id);

    @Transactional(readOnly = true)
    CategoryAttribute getByIdIncludingDeleted(Long id);

    @Transactional
    CategoryAttribute create(@Valid CategoryAttributeRequestDTO dto);

    @Transactional
    CategoryAttribute update(Long id, @Valid CategoryAttributeUpdateDTO dto);

    @Transactional
    void delete(Long id);

    @Transactional
    void deleteByCategory(Category category);

    @Transactional
    CategoryAttribute restore(Long id) throws BadRequestException;


    @Transactional(readOnly = true)
    Page<CategoryAttribute> searchPublic(SearchRequest<CategoryAttributePublicFilter> req);

    @Transactional(readOnly = true)
    Page<CategoryAttribute> searchAdmin(SearchRequest<CategoryAttributeAdminFilter> req);

    @Transactional(readOnly = true)
    <F> Page<CategoryAttribute> search(SearchRequest<F> req, Class<F> filterClass);

    @Transactional(readOnly = true)
    CategoryAttributeUsageDTO summarizeCategoryAttributeUsage(Long id);

    void restoreByCategory(Category category);
}
