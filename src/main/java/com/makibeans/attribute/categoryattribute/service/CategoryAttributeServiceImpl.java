// language: java
package com.makibeans.attribute.categoryattribute.service;

import com.makibeans.attribute.categoryattribute.dto.CategoryAttributeRequestDTO;
import com.makibeans.attribute.categoryattribute.dto.CategoryAttributeUpdateDTO;
import com.makibeans.attribute.categoryattribute.filter.CategoryAttributeFilter;
import com.makibeans.attribute.categoryattribute.mapper.CategoryAttributeMapper;
import com.makibeans.attribute.categoryattribute.model.CategoryAttribute;
import com.makibeans.attribute.categoryattribute.repository.CategoryAttributeRepository;
import com.makibeans.search.*;
import com.makibeans.web.exceptions.DuplicateResourceException;
import com.makibeans.attribute.attribute.model.Attribute;
import com.makibeans.category.model.Category;
import com.makibeans.attribute.attribute.service.AttributeService;
import com.makibeans.category.service.CategoryService;
import com.makibeans.common.service.CrudService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryAttributeServiceImpl
        implements CategoryAttributeService, CrudService<CategoryAttribute, Long> {

    private final CategoryAttributeRepository repo;
    private final CategoryService categoryService;
    private final AttributeService attributeService;
    private final CategoryAttributeMapper mapper;

    @Autowired
    public CategoryAttributeServiceImpl(
            CategoryAttributeRepository repo,
            CategoryService categoryService,
            AttributeService attributeService,
            CategoryAttributeMapper mapper
    ) {
        this.repo = repo;
        this.categoryService = categoryService;
        this.attributeService = attributeService;
        this.mapper = mapper;
    }

    @Override
    public JpaRepository<CategoryAttribute, Long> repo() {
        return this.repo;
    }

    // -------------------------------------------------------------------------
    // READS
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public CategoryAttribute getById(Long id) {
        return getOrThrow(id);
    }

    /**
     * Searches for CategoryAttributes based on the provided search request.
     * Supports filtering, sorting, and pagination.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CategoryAttribute> search(SearchRequest<CategoryAttributeFilter> req) {
        Specification<CategoryAttribute> spec =
                SpecificationFactory.fromRequest(req, CategoryAttributeFilter.class);

        Sort sort = new SortResolver(CategoryAttributeFilter.class)
                .resolve(req.getSortBy(), req.getSortDirection());

        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 20,
                sort
        );

        return repo.findAll(spec, pageable);
    }

    // -------------------------------------------------------------------------
    // WRITES
    // -------------------------------------------------------------------------

    /**
     * Creates a new CategoryAttribute association between a category and an attribute.
     */
    @Override
    @Transactional
    public CategoryAttribute create(@Valid CategoryAttributeRequestDTO requestDTO) {
        Category category = categoryService.getOrThrow(requestDTO.getCategoryId());
        Attribute attribute = attributeService.getById(requestDTO.getAttributeId());

        if (repo.existsByCategoryIdAndAttributeId(category.getId(), attribute.getId())) {
            throw new DuplicateResourceException(
                    "Attribute '" + attribute.getName()
                            + "' is already associated with Category '" + category.getName() + "'."
            );
        }

        CategoryAttribute categoryAttribute = CategoryAttribute.builder()
                .category(category)
                .attribute(attribute)
                .required(requestDTO.getRequired())
                .build();

        return repo.save(categoryAttribute);
    }

    /**
     * Updates an existing CategoryAttribute.
     */
    @Override
    @Transactional
    public CategoryAttribute update(Long id, @Valid CategoryAttributeUpdateDTO updateDTO) {
        CategoryAttribute categoryAttribute = getOrThrow(id);

        // Entity is managed; no explicit save needed if using JPA dirty checking.
        mapper.updateEntityFromDTO(updateDTO, categoryAttribute);

        return categoryAttribute;
    }

    // -------------------------------------------------------------------------
    // OTHER
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Boolean existByCategoryId(Long categoryId) {
        return repo.existsByCategoryId(categoryId);
    }

    // delete(...) etc. remain unchanged
}
