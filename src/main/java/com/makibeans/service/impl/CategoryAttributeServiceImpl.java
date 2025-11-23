package com.makibeans.service.impl;

import com.makibeans.search.*;
import com.makibeans.dto.categoryattribute.CategoryAttributeRequestDTO;
import com.makibeans.dto.categoryattribute.CategoryAttributeResponseDTO;
import com.makibeans.dto.categoryattribute.CategoryAttributeUpdateDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.mapper.CategoryAttributeMapper;
import com.makibeans.model.Attribute;
import com.makibeans.model.Category;
import com.makibeans.model.CategoryAttribute;
import com.makibeans.repository.CategoryAttributeRepository;
import com.makibeans.search.filters.CategoryAttributeFilter;
import com.makibeans.service.service.AttributeService;
import com.makibeans.service.service.CategoryAttributeService;
import com.makibeans.service.service.CategoryService;
import com.makibeans.service.service.CrudService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing CategoryAttribute entities.
 * Provides methods to perform CRUD operations and manage associations between categories and attributes.
 */
@Service
public class CategoryAttributeServiceImpl implements CategoryAttributeService, CrudService<CategoryAttribute, Long> {

    private final CategoryAttributeRepository repo;
    private final CategoryService categoryService;
    private final AttributeService attributeService;
    private final CategoryAttributeMapper mapper;


    @Autowired
    public CategoryAttributeServiceImpl(
            CategoryAttributeRepository repo, CategoryService categoryService, AttributeService attributeService,
            CategoryAttributeMapper mapper) {
        this.repo = repo;
        this.categoryService = categoryService;
        this.attributeService = attributeService;
        this.mapper = mapper;
    }

    @Override
    public JpaRepository<CategoryAttribute, Long> repo() {
        return this.repo; // or whatever your injected repository field is named
    }

    @Override
    public CategoryAttributeResponseDTO geById(Long id) {
        CategoryAttribute categoryAttribute = getOrThrow(id);
        return mapper.toResponseDTO(categoryAttribute);
    }

    /**
     * Searches for CategoryAttributes based on the provided search request.
     * Supports filtering, sorting, and pagination.
     *
     * @param req the search request containing filters, sort options, and pagination details; must not be null
     * @return a paginated list of CategoryAttribute response DTOs matching the search criteria
     * @throws IllegalArgumentException if the req is null
     */
    @Override
    public Page<CategoryAttributeResponseDTO> search(SearchRequest<CategoryAttributeFilter> req) {
        Specification<CategoryAttribute> spec =
                SpecificationFactory.fromRequest(req, CategoryAttributeFilter.class);

        Sort sort = new SortResolver(CategoryAttributeFilter.class)
                .resolve(req.getSortBy(), req.getSortDirection());

        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 20,
                sort
        );

        return repo.findAll(spec, pageable).map(mapper::toResponseDTO);
    }

    /**
     * Creates a new CategoryAttribute association between a category and an attribute.
     *
     * @param requestDTO the DTO containing the details of the CategoryAttribute to create; must not be null
     * @return the created CategoryAttribute as a response DTO
     * @throws IllegalArgumentException   if the requestDTO is null
     * @throws DuplicateResourceException if the attribute is already associated with the category
     */
    @Override
    public CategoryAttributeResponseDTO create(@Valid CategoryAttributeRequestDTO requestDTO) {
        Category category = categoryService.getOrThrow(requestDTO.getCategoryId());
        Attribute attribute = attributeService.getOrThrow(requestDTO.getAttributeId());

        if (repo.existsByCategoryIdAndAttributeId(category.getId(), attribute.getId())) {
            throw new DuplicateResourceException("Attribute " + attribute.getName() + " is already associated with Category " + category.getName());
        }

        CategoryAttribute categoryAttribute = CategoryAttribute.builder()
                .category(category)
                .attribute(attribute)
                .required(requestDTO.getRequired())
                .build();

        CategoryAttribute savedCategoryAttribute = repo.save(categoryAttribute);
        return mapper.toResponseDTO(savedCategoryAttribute);

    }

    /**
     * Updates an existing CategoryAttribute.
     *
     * @param id        the ID of the CategoryAttribute to update; must not be null
     * @param updateDTO the DTO containing the updated details; must not be null
     * @return the updated CategoryAttribute as a response DTO
     * @throws IllegalArgumentException if the id or updateDTO is null
     */
    @Override
    @Transactional
    public CategoryAttributeResponseDTO update(Long id, @Valid CategoryAttributeUpdateDTO updateDTO) {

        CategoryAttribute categoryAttribute = getOrThrow(id);

        //Because the entity is loaded and managed inside a @Transactional method, JPA/Hibernate uses dirty checking. If the mapper actually changes one or more fields (values differ), an UPDATE is issued on flush/commit. If nothing changes (all values remain equal), no SQL UPDATE is executed. No explicit save call is needed.
        mapper.updateEntityFromDTO(updateDTO, categoryAttribute);

        return mapper.toResponseDTO(categoryAttribute);
    }

    /**
     * Deletes a CategoryAttribute by its ID.
     *
     * @param id the ID of the CategoryAttribute to delete; must not be null
     * @throws IllegalArgumentException if the id is null
     */

    @Override
    @Transactional
    public void delete(Long id) {
        hardDelete(id);
    }

    /**
     * Checks if any CategoryAttribute exists for the given category ID.
     *
     * @param categoryId the ID of the category to check; must not be null
     * @return true if any CategoryAttribute exists for the category, false otherwise
     * @throws IllegalArgumentException if the categoryId is null
     */
    public Boolean existByCategoryId(Long categoryId){
        return repo.existsByCategoryId(categoryId);
    }
}