// language: java
package com.makibeans.categoryattribute.service;

import com.makibeans.audit.model.DeleteReason;
import com.makibeans.categoryattribute.dto.*;
import com.makibeans.categoryattribute.model.CategoryAttribute;
import com.makibeans.category.model.Category;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Domain API for CategoryAttribute links.
 * Services now return entities; controllers are responsible for mapping to DTOs.
 */
public interface CategoryAttributeService  {


    @Transactional(readOnly = true)
    List<CategoryAttribute> getAllByCategoryId(Long categoryId);

 /*   @Transactional(readOnly = true)
    CategoryAttribute getById(Long id);*/

    @Transactional(readOnly = true)
    CategoryAttribute getById(Long id);

    @Transactional(readOnly = true)
    CategoryAttribute getByCategoryIdAndAttributeIdAndDeletedFalse(Long categoryId, Long attributeId);

    CategoryAttribute getByCategoryIdAndAttributeIdIncludingDeleted(Long id, Long attributeId);

    @Transactional(readOnly = true)
    List<CategoryAttribute> getAllByCategoryIdIncludingDeleted(Long categoryId);

    @Transactional(readOnly = true)
    CategoryAttributeUsageDTO summarizeUsage(Long categoryId, Long attributeId);

    @Transactional(readOnly = true)
    CategoryAttribute add(Long categoryId, @Valid CategoryAttributeRequestDTO dto);

    @Transactional(readOnly = true)
    List<CategoryAttribute> replaceAll(Long id, List<CategoryAttributeRequestDTO> body);

    @Transactional(readOnly = true)
    CategoryAttribute update(Long categoryId, Long attributeId, @Valid CategoryAttributeUpdateDTO updateDTO);

    @Transactional(readOnly = true)
    void delete(Long id, Long attributeId);

    @Transactional(readOnly = true)
    void deleteAllByCategory(Category category);

    void restoreAllByCategory(Category category, DeleteReason reason) throws BadRequestException;



}
