package com.makibeans.attribute.service;

import com.makibeans.attribute.dto.AttributeRequestDTO;
import com.makibeans.attribute.dto.AttributeUpdateDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.attribute.mapper.AttributeMapper;
import com.makibeans.attribute.model.Attribute;
import com.makibeans.attribute.repository.AttributeRepository;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import com.makibeans.search.filters.AttributeFilter;
import com.makibeans.attributevalue.service.AttributeValueService;
import com.makibeans.common.service.CrudService;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.makibeans.util.UpdateUtils.*;

/**
 * Service class for managing Attribute entities.
 * Provides methods to perform CRUD operations and search for AttributeTemplates.
 */
@Service
public class AttributeServiceImpl implements CrudService<Attribute, Long>, AttributeService {

    private final AttributeMapper mapper;
    private final AttributeRepository repo;

    @Autowired
    public AttributeServiceImpl(
            AttributeMapper mapper, AttributeRepository repo, @Lazy AttributeValueService attributeValueService) {
        this.mapper = mapper;
        this.repo = repo;
    }

    @Override
    public JpaRepository<Attribute, Long> repo() {
        return this.repo;
    }

    /**
     * Retrieves an Attribute by its unique identifier.
     *
     * @param id the unique identifier of the Attribute to retrieve.
     * @return the AttributeTemplateResponseDTO representing the found attribute template.
     * @throws IllegalArgumentException  if the provided id is null
     * @throws ResourceNotFoundException if no Attribute is found with the given id.
     */

    @Transactional(readOnly = true)
    public Attribute getById(Long id) {
        return getOrThrow(id);
    }

    /**
     * Searches for Attributes based on the provided search request.
     *
     * @param req the search request containing filters, pagination, and sorting information
     * @return a paginated list of Attributes matching the search criteria
     */
    @Transactional
    public Page<Attribute> search(SearchRequest<AttributeFilter> req) {
        Specification<Attribute> spec =
                SpecificationFactory.fromRequest(req, AttributeFilter.class);

        Sort sort = new SortResolver(AttributeFilter.class)
                .resolve(req.getSortBy(), req.getSortDirection());

        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 20,
                sort
        );

        return repo.findAll(spec, pageable);
    }


    /**
     * Creates a new Attribute and refreshed the cache of valid attribute keys.
     *
     * @param dto the DTO containing the attribute template details
     * @return the created Attribute entity as AttributeTemplateResponseDTO
     * @throws DuplicateResourceException if an Attribute with the same name already exists
     */

    @Transactional
    public Attribute create(AttributeRequestDTO dto) {

        assertUniqueName(dto.getName());

        Attribute attribute = new Attribute();

        attribute.setName(dto.getName());

        return repo.save(attribute);
    }


    /**
     * Updates an existing Attribute.
     *
     * @param id  the ID of the attribute template to update
     * @param dto the DTO containing the updated attribute template name
     * @return the updated Attribute entity as AttributeTemplateResponseDTO
     * @throws ResourceNotFoundException  if the attribute template does not exist
     * @throws DuplicateResourceException if another Attribute already exists with the same name
     */

    @Transactional
    public Attribute update(Long id, @Valid AttributeUpdateDTO dto) {

        Attribute attribute = getOrThrow(id);

        String newName = normalize(attribute.getName());

        assertUniqueNameAndIdNot(id, newName);

        mapper.updateEntityFromDTO(dto, attribute);

        return attribute;
    }

    private void assertUniqueName(String name) {
        if (name != null && repo.existsByName(name)) {
            throw new DuplicateResourceException(
                    "Attribute with name '" + name + "' already exists.");
        }
    }

    private void assertUniqueNameAndIdNot(Long id, String name) {
        if (name != null && repo.existsByNameAndIdNot(name, id)) {
            throw new DuplicateResourceException(
                    "Attribute with name '" + name + "' already exists.");
        }
    }
}
