package com.makibeans.service;

import com.makibeans.dto.attributevalue.AttributeValueRequestDTO;
import com.makibeans.dto.attributevalue.AttributeValueResponseDTO;
import com.makibeans.dto.attributevalue.AttributeValueUpdateDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceInUseException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.filter.SearchFilter;
import com.makibeans.mapper.AttributeValueMapper;
import com.makibeans.model.Attribute;
import com.makibeans.model.AttributeValue;
import com.makibeans.repository.AttributeValueRepository;
import com.makibeans.repository.ProductAttributeValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.makibeans.util.UpdateUtils.normalize;
import static com.makibeans.util.UpdateUtils.shouldUpdate;

/**
 * Service class for managing AttributeValues.
 */

@Service
public class AttributeValueService extends AbstractCrudService<AttributeValue, Long> {

    private final AttributeValueRepository attributeValueRepository;
    private final AttributeService attributeService;
    private final AttributeValueMapper mapper;
    private final ProductAttributeValueRepository productAttributeValueRepository;


    @Autowired
    public AttributeValueService(AttributeValueRepository attributeValueRepository,
                                 AttributeService attributeService,
                                 ProductAttributeValueRepository productAttributeValueRepository,
                                 AttributeValueMapper mapper) {
        super(attributeValueRepository);
        this.attributeValueRepository = attributeValueRepository;
        this.attributeService = attributeService;
        this.mapper = mapper;
        this.productAttributeValueRepository = productAttributeValueRepository;
    }

    /**
     * Retrieves an AttributeValue by its unique identifier.
     *
     * @param id the unique identifier of the AttributeValue to retrieve.
     * @return the AttributeValueResponseDTO representing the found attribute template.
     * @throws IllegalArgumentException  if the provided id is null
     * @throws ResourceNotFoundException if no AttributeValue is found with the given id.
     */

    @Transactional(readOnly = true)
    public AttributeValueResponseDTO getAttributeValueById(Long id) {
        AttributeValue attributeValue = findById(id);
        return mapper.toResponseDTO(attributeValue);
    }

    /**
     * Searches for AttributeValues based on the provided filters.
     * The search is performed on the value field of the AttributeValue.
     *
     * @param searchParams the map containing the search parameters (e.g., "search", "sort", "order")
     * @return a list of AttributeValueResponseDTOs representing the matched attribute values
     */

    @Transactional(readOnly = true)
    public List<AttributeValueResponseDTO> findBySearchQuery(Map<String, String> searchParams) {

        Map<String, Function<AttributeValue, String>> searchFields = Map.of(
                "value", AttributeValue::getValue,
                "attributeTemplate", attributeValue -> attributeValue.getAttribute().getName()
        );

        Map<String, Comparator<AttributeValue>> sortFields = Map.of(
                "id", Comparator.comparing(AttributeValue::getId, Comparator.nullsLast(Comparator.naturalOrder())),
                "value", Comparator.comparing(AttributeValue::getValue, String.CASE_INSENSITIVE_ORDER),
                "attributeTemplate", Comparator.comparing(attributeValue -> attributeValue.getAttribute().getName()));

        // Apply filtering and sorting using SearchFilter
        List<AttributeValue> matchedValues = SearchFilter.apply(
                findAll(),
                searchParams,
                searchFields,
                sortFields);

        return matchedValues.stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    /**
     * Retrieves all AttributeValues by given Attribute id
     *
     * @param templateId the attributeTemplate id to search on
     * @return the list of all AttributeValueResponseDTO's representing the found attribute values.
     */

    @Transactional(readOnly = true)
    public List<AttributeValueResponseDTO> getAllAttributeValuesByAttributeId(Long templateId) {

        Attribute attribute = attributeService.findById(templateId);

        return attributeValueRepository.findAllByAttribute(attribute)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AttributeValue> getAllNonDeletedAttributeValuesByAttributeId(Long attributeId) {
        return attributeValueRepository.findByAttributeIdAndDeletedFalse(attributeId);
    }

    /**
     * Counts the number of non-deleted AttributeValues associated with a specific Attribute ID.
     *
     * @param attributeId the ID of the attribute
     * @return the count of non-deleted AttributeValues associated with the given Attribute ID
     */

    @Transactional
    public Long countNonDeletedAttributeValuesByAttributeId(Long attributeId) {
        return attributeValueRepository.countAttributeValuesByAttributeIdAndDeletedIsFalse(attributeId);
    }

    /**
     * Creates a new AttributeValue.
     *
     * @param requestDTO the DTO containing the attribute value details
     * @return the AttributeValueResponseDTO that represents the created AttributeValue entity
     * @throws DuplicateResourceException if an AttributeValue with the same value already exists
     */

    @Transactional
    public AttributeValueResponseDTO createAttributeValue(AttributeValueRequestDTO requestDTO) {

        Attribute attribute = attributeService.findById(requestDTO.getAttributeId());

        String normalizedValue = normalize(requestDTO.getValue());

        validateUniqueAttributeValue(attribute, normalizedValue);

        AttributeValue attributeValue = new AttributeValue(attribute, normalizedValue);

        AttributeValue savedAttributeValue = create(attributeValue);

        return mapper.toResponseDTO(savedAttributeValue);
    }

    /**
     * Deletes an AttributeValue by ID and removes it from product_attribute_value table
     *
     * @param id the ID of the attribute value to delete
     * @throws ResourceNotFoundException if the attribute value does not exist
     */

    @Transactional
    public void deleteAttributeValue(Long id) {
        findById(id);
        //TODO: check which products still use this attribute value and notify the user. Maybe create DTO with the list of affected products
        if (productAttributeValueRepository.existsByAttributeValueId(id)) {
            throw new ResourceInUseException("Cannot delete attribute value with id " + id + " as it is referenced by product attributes.");
        }
        //TODO: remove since not needed with the existsBy check above
        //productAttributeService.deleteAttributeValuesByAttributeValueId(id);
        delete(id);
    }

    /**
     * Soft deletes all AttributeValues associated with the given Attribute ID.
     * If any of the AttributeValues are referenced by ProductAttributes, a ResourceInUseException is thrown.
     *
     * @param attributeId the ID of the attribute whose values are to be soft deleted
     * @throws ResourceInUseException if any AttributeValue is referenced by ProductAttributes
     */

    @Transactional
    public void softDeleteByAttributeId(Long attributeId) {
        List<AttributeValue> attributeValues = this.getAllNonDeletedAttributeValuesByAttributeId(attributeId);
        //TODO check if any of the attribute values are in use by product attributes
        attributeValues.forEach(attributeValue -> attributeValue.setDeleted(true));
    }

    /**
     * Updates an existing AttributeValue.
     *
     * @param id  the ID of the attribute value to update
     * @param dto the DTO containing the updated attribute value details
     * @return the updated AttributeValueResponseDTO representing the updated attribute value
     * @throws ResourceNotFoundException  if the attribute value does not exist
     * @throws DuplicateResourceException if another AttributeValue with the same value already exists
     */

    @Transactional
    public AttributeValueResponseDTO updateAttributeValue(Long id, AttributeValueUpdateDTO dto) {

        AttributeValue attributeValue = findById(id);

        String newValue = normalize(dto.getValue());

        boolean updated = updateAttributeValueField(attributeValue, newValue);

        AttributeValue updatedAttributeValue = updated ? update(id, attributeValue) : attributeValue;

        return mapper.toResponseDTO(updatedAttributeValue);
    }

    /**
     * Validates that an attribute value is unique within the given attribute template.
     *
     * @param attribute the attribute template to check within
     * @param value             the value to validate
     * @throws DuplicateResourceException if an attribute value with the same value already exists within the attribute template
     */

    private void validateUniqueAttributeValue(Attribute attribute, String value) {
        if (attributeValueRepository.existsByAttributeAndValue(attribute, value)) {
            throw new DuplicateResourceException("Attribute value '" + value + "' already exists for attribute " + attribute.getName() + ".");
        }
    }

    /**
     * Updates the value of the given AttributeValue if needed.
     *
     * @param attributeValue the AttributeValue to update
     * @param newValue       the new value to set
     * @return true if the value was updated, false otherwise
     * @throws DuplicateResourceException if another AttributeValue with the same value already exists
     */

    private boolean updateAttributeValueField(AttributeValue attributeValue, String newValue) {
        if (shouldUpdate(newValue, attributeValue.getValue())) {
            validateUniqueAttributeValue(attributeValue.getAttribute(), newValue);
            attributeValue.setValue(newValue);
            return true;
        }
        return false;
    }
}
