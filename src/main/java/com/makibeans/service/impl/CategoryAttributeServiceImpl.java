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
import com.makibeans.service.AbstractCrudService;
import com.makibeans.service.AttributeService;
import com.makibeans.service.CategoryAttributeService;
import com.makibeans.service.CategoryService;
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
import java.util.List;

/**
 * Service class for managing CategoryAttribute entities.
 * Provides methods to perform CRUD operations and manage associations between categories and attributes.
 */
@Service
public class CategoryAttributeServiceImpl extends AbstractCrudService<CategoryAttribute, Long> implements CategoryAttributeService {

    private final CategoryAttributeRepository categoryAttributeRepository;
    private final CategoryService categoryService;
    private final AttributeService attributeService;
    private final CategoryAttributeMapper mapper;


    @Autowired
    public CategoryAttributeServiceImpl(JpaRepository<CategoryAttribute, Long> repository,
                                        CategoryAttributeRepository categoryAttributeRepository, CategoryService categoryService, AttributeService attributeService,
                                        CategoryAttributeMapper mapper) {
        super(repository);
        this.categoryAttributeRepository = categoryAttributeRepository;
        this.categoryService = categoryService;
        this.attributeService = attributeService;
        this.mapper = mapper;
    }

    /**
     * Creates a new CategoryAttribute association between a category and an attribute.
     *
     * @param requestDTO the DTO containing the details of the CategoryAttribute to create; must not be null
     * @return the created CategoryAttribute as a response DTO
     * @throws IllegalArgumentException    if the requestDTO is null
     * @throws DuplicateResourceException if the attribute is already associated with the category
     */
    @Override
    public CategoryAttributeResponseDTO create(@Valid CategoryAttributeRequestDTO requestDTO) {
        Category category = categoryService.findById(requestDTO.getCategoryId());
        Attribute attribute = attributeService.findById(requestDTO.getAttributeId());

        if (categoryAttributeRepository.existsByCategoryIdAndAttributeId(category.getId(), attribute.getId())) {
            throw new DuplicateResourceException("Attribute " + attribute.getName() + " is already associated with Category " + category.getName());
        }

        CategoryAttribute categoryAttribute = CategoryAttribute.builder()
                .category(category)
                .attribute(attribute)
                .required(requestDTO.getRequired())
                .build();

        CategoryAttribute savedCategoryAttribute = categoryAttributeRepository.save(categoryAttribute);
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

        CategoryAttribute categoryAttribute = findById(id);

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
        // no soft delete
        super.delete(id);
    }

    /**
     * Retrieves a CategoryAttribute by its ID.
     *
     * @param id the ID of the CategoryAttribute to retrieve; must not be null
     * @return the CategoryAttribute as a response DTO
     * @throws IllegalArgumentException if the id is null
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryAttributeResponseDTO getById(Long id) {
        CategoryAttribute categoryAttribute = findById(id);
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

    return categoryAttributeRepository.findAll(spec, pageable).map(mapper::toResponseDTO);
}
    /**
     * Retrieves all CategoryAttributes associated with a specific category.
     *
     * @param categoryId the ID of the category; must not be null
     * @return a list of CategoryAttribute response DTOs associated with the category
     * @throws IllegalArgumentException if the categoryId is null
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryAttributeResponseDTO> getByCategory(Long categoryId) {
        Category category = categoryService.findById(categoryId);
        List<CategoryAttribute> categoryAttributes = categoryAttributeRepository.findAllByCategoryId(category.getId());
        return categoryAttributes.stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    /**
     * Retrieves all CategoryAttributes associated with a specific attribute.
     *
     * @param attributeId the ID of the attribute; must not be null
     * @return a list of CategoryAttribute response DTOs associated with the attribute
     * @throws IllegalArgumentException if the attributeId is null
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryAttributeResponseDTO> getByAttribute(Long attributeId) {
        Attribute attribute = attributeService.findById(attributeId);
        List<CategoryAttribute> categoryAttributes = categoryAttributeRepository.findAllByAttributeId(attribute.getId());
        return categoryAttributes.stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    /**
     * Retrieves all CategoryAttributes in the system.
     *
     * @return a list of all CategoryAttribute response DTOs
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryAttributeResponseDTO> getAll() {
        return categoryAttributeRepository.findAll().stream()
                .map(mapper::toResponseDTO)
                .toList();
    }
}