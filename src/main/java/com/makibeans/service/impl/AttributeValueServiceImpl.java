package com.makibeans.service.impl;

import com.makibeans.dto.attributevalue.AttributeValueRequestDTO;
import com.makibeans.dto.attributevalue.AttributeValueResponseDTO;
import com.makibeans.dto.attributevalue.AttributeValueUpdateDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceInUseException;
import com.makibeans.mapper.AttributeValueMapper;
import com.makibeans.model.Attribute;
import com.makibeans.model.AttributeValue;
import com.makibeans.repository.AttributeValueRepository;
import com.makibeans.repository.ProductAttributeValueRepository;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import com.makibeans.search.filters.AttributeValueFilter;
import com.makibeans.service.service.AttributeService;
import com.makibeans.service.service.AttributeValueService;
import com.makibeans.service.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.makibeans.util.UpdateUtils.*;

/**
 * Service class for managing AttributeValues.
 */

@Service
public class AttributeValueServiceImpl implements CrudService<AttributeValue, Long>, AttributeValueService {

    private final AttributeValueRepository repo;
    private final AttributeService attributeService;
    private final ProductAttributeValueRepository productAttributeValueRepository;
    private final AttributeValueMapper mapper;


    @Autowired
    public AttributeValueServiceImpl(
            AttributeValueRepository repo,
            AttributeService attributeService, ProductAttributeValueRepository productAttributeValueRepository,
            AttributeValueMapper mapper) {
        this.repo = repo;
        this.attributeService = attributeService;
        this.productAttributeValueRepository = productAttributeValueRepository;
        this.mapper = mapper;
    }

    @Override
    public JpaRepository<AttributeValue, Long> repo() {
        return this.repo;
    }

    @Transactional(readOnly = true)
    public AttributeValueResponseDTO getById(Long id) {
        AttributeValue attributeValue = getOrThrow(id);
        return mapper.toResponseDTO(attributeValue);
    }

    @Transactional
    public Page<AttributeValueResponseDTO> search(SearchRequest<AttributeValueFilter> req) {
        Specification<AttributeValue> spec =
                SpecificationFactory.fromRequest(req, AttributeValueFilter.class);

        Sort sort = new SortResolver(AttributeValueFilter.class)
                .resolve(req.getSortBy(), req.getSortDirection());

        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 20,
                sort
        );

        return repo.findAll(spec, pageable).map(mapper::toResponseDTO);
    }

    @Transactional
    public AttributeValueResponseDTO create(AttributeValueRequestDTO requestDTO) {
        Attribute attribute = attributeService.getOrThrow(requestDTO.getAttributeId());

        String normalizedValue = normalize(requestDTO.getValue());

        asserUniqueNameAndAttributeId(normalizedValue, attribute.getId());

        AttributeValue attributeValue = new AttributeValue(attribute, normalize(requestDTO.getValue()));

        attributeValue.setAttribute(attribute);

        AttributeValue savedAttributeValue = repo.save(attributeValue);

        return mapper.toResponseDTO(savedAttributeValue);
    }

    @Transactional
    public AttributeValueResponseDTO update(Long id, AttributeValueUpdateDTO updateDTO) {
        AttributeValue attributeValue = getOrThrow(id);

        String oldName = normalize(attributeValue.getValue());

        mapper.updateEntityFromDTO(updateDTO, attributeValue);

        String newName = normalize(attributeValue.getValue());

        if (hasChanged(oldName, newName)) {
            asserUniqueValueAndAttributeIdAndIdNot(newName, attributeValue.getAttribute().getId(), attributeValue.getId());
            attributeValue.setValue(newName);
        }

        try {
            repo.flush(); // surface DB unique index violations here
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateResourceException("Attribute value already exists.");
        }

        return mapper.toResponseDTO(attributeValue);
    }


    @Transactional
    public void delete(Long id) {

        boolean inUseByProductAttributeValues = productAttributeValueRepository.existsByAttributeValueId(id);

        if (inUseByProductAttributeValues) {
            throw new ResourceInUseException("Attribute value with id '" + id + "' is in use and cannot be deleted.");
        }
        hardDelete(id);
    }

    private void asserUniqueNameAndAttributeId(String value, Long attributeId) {
        if (value != null && repo.existsByValueAndAttributeId(value, attributeId)) {
            throw new DuplicateResourceException(
                    "Value'" + value + "' already exists for attribute with id '" + attributeId + "'.");
        }
    }

    private void asserUniqueValueAndAttributeIdAndIdNot(String name, Long attributeId, Long id) {
        if (name != null && repo.existsByValueAndAttributeIdAndIdNot(name, attributeId, id)) {
            throw new DuplicateResourceException(
                    "Value '" + name + "' already exists for attribute with id '" + attributeId + "'.");
        }

    }
}