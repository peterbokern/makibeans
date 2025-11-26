// language: java
package com.makibeans.attribute.categoryattribute.service;

import com.makibeans.attribute.categoryattribute.dto.CategoryAttributeRequestDTO;
import com.makibeans.attribute.categoryattribute.dto.CategoryAttributeUpdateDTO;
import com.makibeans.attribute.categoryattribute.model.CategoryAttribute;
import com.makibeans.search.SearchRequest;
import com.makibeans.attribute.categoryattribute.filter.CategoryAttributeFilter;
import com.makibeans.common.service.CrudService;
import org.springframework.data.domain.Page;

/**
 * Domain API for CategoryAttribute links.
 * Services now return entities; controllers are responsible for mapping to DTOs.
 */
public interface CategoryAttributeService extends CrudService<CategoryAttribute, Long> {

    CategoryAttribute getById(Long id);

    Page<CategoryAttribute> search(SearchRequest<CategoryAttributeFilter> req);

    CategoryAttribute create(CategoryAttributeRequestDTO body);

    CategoryAttribute update(Long id, CategoryAttributeUpdateDTO body);

    Boolean existByCategoryId(Long categoryId);
}
