package com.makibeans.attribute.attributevalue.service;

import com.makibeans.attribute.attribute.model.AttributeDataType;
import com.makibeans.attribute.attributevalue.dto.AttributeValueUpdateDTO;
import com.makibeans.attribute.attributevalue.mapper.AttributeValueMapper;
import com.makibeans.attribute.attributevalue.repository.AttributeValueRepository;
import com.makibeans.attribute.attributevalue.dto.AttributeValueRequestDTO;
import com.makibeans.attribute.attributevalue.util.AttributeValueParser;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.attribute.attribute.model.Attribute;
import com.makibeans.attribute.attributevalue.model.AttributeValue;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import com.makibeans.attribute.attributevalue.filter.AttributeValueFilter;
import com.makibeans.attribute.attribute.service.AttributeService;
import com.makibeans.common.util.TextUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Domain service for managing {@link AttributeValue} entities.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Lookup and search of attribute values via the generic search infrastructure.</li>
 *   <li>Creation and update of values with type-safe parsing based on {@link AttributeDataType}.</li>
 *   <li>Uniqueness checks per attribute and data type.</li>
 *   <li>Soft delete / restore semantics.</li>
 *   <li>Sort-order handling within a single {@link Attribute}.</li>
 * </ul>
 */
@Service
public class AttributeValueServiceImpl implements  AttributeValueService {

    private final AttributeValueRepository repo;
    private final AttributeService attributeService;
    private final AttributeValueMapper mapper;

    @Autowired
    public AttributeValueServiceImpl(
            AttributeValueRepository repo,
            AttributeService attributeService,
            AttributeValueMapper mapper
    ) {
        this.repo = repo;
        this.attributeService = attributeService;
        this.mapper = mapper;
    }

    /**
     * Returns an {@link AttributeValue} by id or throws if it does not exist.
     *
     * @param id the database identifier of the attribute value
     * @return the resolved {@link AttributeValue}
     * @throws ResourceNotFoundException if no value with the given id exists
     */
    @Override
    @Transactional(readOnly = true)
    public AttributeValue getById(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Attribute value with ID " + id + " not found."));
    }

    /**
     * Executes a paged, filtered search for {@link AttributeValue} entities.
     * <p>
     * The provided {@link SearchRequest} is translated into a {@link Specification}
     * using {@link SpecificationFactory} and the metadata from {@link AttributeValueFilter}.
     *
     * @param req the search request containing filters, paging and sorting options
     * @return a page of matching {@link AttributeValue} entities
     */
    @Transactional(readOnly = true)
    public Page<AttributeValue> search(SearchRequest<AttributeValueFilter> req) {
        Specification<AttributeValue> spec =
                SpecificationFactory.fromRequest(req, AttributeValueFilter.class);

        Sort sort = new SortResolver(AttributeValueFilter.class)
                .resolve(req.getSortBy(), req.getSortDirection());

        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 20,
                sort
        );

        return repo.findAll(spec, pageable);
    }

    /**
     * Creates a new {@link AttributeValue} for the given attribute.
     * <p>
     * Steps:
     * <ol>
     *   <li>Resolve {@link Attribute} from the id contained in the request.</li>
     *   <li>Normalise the raw value and ensure uniqueness for the attribute + data type.</li>
     *   <li>Parse and apply the type-specific value field (string/number/boolean/date/... ).</li>
     *   <li>Generate slug and sort-order.</li>
     *   <li>Persist and return the entity.</li>
     * </ol>
     *
     * @param requestDTO value creation request
     * @return the persisted {@link AttributeValue}
     * @throws BadRequestException        if the raw value cannot be parsed or is empty
     * @throws DuplicateResourceException if the value already exists for the given attribute
     */
    @Transactional
    public AttributeValue create(AttributeValueRequestDTO requestDTO) throws BadRequestException {
        Attribute attribute = attributeService.getOrThrow(requestDTO.getAttributeId());
        AttributeDataType type = attribute.getDataType();

        String normalizedValue = normalizeRawValue(requestDTO.getRawValue());
        assertUniqueRawValueForCreate(normalizedValue, attribute, type);

        AttributeValue av = new AttributeValue();

        av.setAttribute(attribute);
        applyRawValue(av, type, normalizedValue);
        av.setSlug(TextUtils.toSlug(normalizedValue));
        av.setSortOrder(resolveSortOrder(attribute, requestDTO.getSortOrder()));

        return repo.save(av);
    }

    /**
     * Updates an existing {@link AttributeValue}.
     * <p>
     * Behaviour:
     * <ul>
     *   <li>If a new raw value is provided, it is normalised, checked for uniqueness,
     *       parsed and stored in the correct typed column, and the slug is updated.</li>
     *   <li>If the sort-order changes, the surrounding values for the same attribute are
     *       shifted accordingly to keep a dense order.</li>
     *   <li>All other updatable fields are delegated to {@link AttributeValueMapper}.</li>
     * </ul>
     *
     * @param id        id of the existing value to update
     * @param updateDTO data to apply
     * @return the updated (but not yet reloaded) {@link AttributeValue}
     * @throws BadRequestException        if the new raw value is invalid
     * @throws DuplicateResourceException if the new value would violate uniqueness
     */
    @Transactional
    public AttributeValue update(Long id, AttributeValueUpdateDTO updateDTO) throws BadRequestException, DuplicateResourceException {
        AttributeValue av = this.getById(id);

        Attribute attribute = av.getAttribute();
        AttributeDataType type = attribute.getDataType();

        //update raw value, slug if present and sort order if changed
        updateRawValueIfPresent(av, attribute, type, updateDTO);
        updateSortOrderIfChanged(av, attribute, updateDTO);

        // update remaining scalar fields (mapper is configured to ignore value + slug)
        mapper.updateEntityFromDTO(updateDTO, av);

        return av;
    }

    /**
     * Performs a soft delete by marking the value as deleted.
     *
     * @param id id of the value to mark as deleted
     * @throws ResourceNotFoundException if no entity with the given id exists
     */
    @Transactional
    public void delete(Long id) {
        AttributeValue av = this.getById(id);
        av.setDeleted(true);
    }

    /**
     * Restores a previously soft-deleted {@link AttributeValue}.
     * <p>
     * Before restoring, the method checks that the effective value for the
     * associated attribute is still unique.
     *
     * @param id id of the value to restore
     * @return the restored {@link AttributeValue}
     * @throws DuplicateResourceException if restoring would violate uniqueness
     */

    @Override
    @Transactional
    public AttributeValue restore(Long id) throws DuplicateResourceException, BadRequestException {
        AttributeValue value = this.getById(id);

        if (!Boolean.TRUE.equals(value.isDeleted())) {
            throw new BadRequestException("Attribute value with ID " + id + " is not deleted and cannot be restored.");
        }

        Attribute attribute = value.getAttribute();
        String rawValue = normalizeRawValue(value.getValueAsString());

        assertUniqueRawValueForUpdate(rawValue, attribute, attribute.getDataType(), value.getId());

        value.setDeleted(false);
        return value;
    }

    /**
     * Parses and assigns the normalised raw value to the correct typed column on the entity.
     *
     * @param target          entity to mutate
     * @param type            attribute value data type
     * @param normalizedValue already-normalised raw value (not blank)
     * @throws BadRequestException if the value is blank or cannot be parsed for the given type
     */
    private void applyRawValue(AttributeValue target, AttributeDataType type, String normalizedValue) throws BadRequestException {

        if (normalizedValue == null || normalizedValue.isBlank()) {
            throw new BadRequestException("Attribute value cannot be empty.");
        }

        // normalise text again for STRING to ensure DB stores trimmed / canonical value
        switch (type) {
            case STRING -> target.setStringValue(TextUtils.normalizeText(normalizedValue));
            case NUMERIC -> target.setNumericValue(AttributeValueParser.parseNumeric(normalizedValue));
            case BOOLEAN -> target.setBooleanValue(AttributeValueParser.parseBoolean(normalizedValue));
            case DATE -> target.setDateValue(AttributeValueParser.parseDate(normalizedValue));
            case DATETIME -> target.setDateTimeValue(AttributeValueParser.parseDateTime(normalizedValue));
        }
    }

    /**
     * Convenience wrapper for normalising free-text values.
     *
     * @param rawValue value as provided by the client (may be null/blank)
     * @return normalised representation, never {@code null}
     */
    private String normalizeRawValue(String rawValue) {
        return TextUtils.normalizeText(rawValue);
    }

    /**
     * Checks whether a value already exists for the given attribute and type.
     *
     * @param value     the (normalised) raw value
     * @param type      the attribute's data type
     * @param attribute the owning attribute
     * @return {@code true} if a matching value exists, {@code false} otherwise
     */
    private boolean valueExists(String value, AttributeDataType type, Attribute attribute) {
        return existsInternal(value, type, attribute, null);
    }

    /**
     * Checks whether a value already exists for the given attribute and type, excluding
     * a specific id (used for update/restore scenarios).
     *
     * @param value     the (normalised) raw value
     * @param type      the attribute's data type
     * @param attribute the owning attribute
     * @param excludeId id to exclude from the check (may be {@code null})
     * @return {@code true} if a conflicting value exists, {@code false} otherwise
     */
    private boolean valueExistsAndIdNot(String value, AttributeDataType type, Attribute attribute, Long excludeId) {
        return existsInternal(value, type, attribute, excludeId);
    }

    /**
     * Shared implementation for existence checks backing {@link #valueExists} and
     * {@link #valueExistsAndIdNot(String, AttributeDataType, Attribute, Long)}.
     */
    private boolean existsInternal(String value, AttributeDataType type, Attribute attribute, Long excludeId) {
        return switch (type) {
            case STRING -> excludeId == null
                    ? repo.existsByAttributeAndStringValueIgnoreCase(attribute, value)
                    : repo.existsByAttributeAndStringValueIgnoreCaseAndIdNot(attribute, value, excludeId);
            case NUMERIC -> excludeId == null
                    ? repo.existsByAttributeAndNumericValue(attribute, AttributeValueParser.parseNumeric(value))
                    : repo.existsByAttributeAndNumericValueAndIdNot(attribute, AttributeValueParser.parseNumeric(value), excludeId);
            case BOOLEAN -> excludeId == null
                    ? repo.existsByAttributeAndBooleanValue(attribute, AttributeValueParser.parseBoolean(value))
                    : repo.existsByAttributeAndBooleanValueAndIdNot(attribute, AttributeValueParser.parseBoolean(value), excludeId);
            case DATE -> excludeId == null
                    ? repo.existsByAttributeAndDateValue(attribute, AttributeValueParser.parseDate(value))
                    : repo.existsByAttributeAndDateValueAndIdNot(attribute, AttributeValueParser.parseDate(value), excludeId);
            case DATETIME -> excludeId == null
                    ? repo.existsByAttributeAndDateTimeValue(attribute, AttributeValueParser.parseDateTime(value))
                    : repo.existsByAttributeAndDateTimeValueAndIdNot(attribute, AttributeValueParser.parseDateTime(value), excludeId);
        };
    }

    /**
     * Ensures that a value about to be created does not already exist for the
     * given attribute + type combination.
     */
    private void assertUniqueRawValueForCreate(String normalizedValue, Attribute attribute, AttributeDataType type) {
        if (valueExists(normalizedValue, type, attribute)) {
            throw new DuplicateResourceException(
                    "Attribute value '" + normalizedValue + "' already exists for attribute '" + attribute.getName() + "'."
            );
        }
    }

    /**
     * Ensures that a value about to be updated does not already exist for the
     * given attribute + type combination, excluding the current entity id.
     */
    private void assertUniqueRawValueForUpdate(String normalizedValue, Attribute attribute, AttributeDataType type, Long currentId) {
        if (valueExistsAndIdNot(normalizedValue, type, attribute, currentId)) {
            throw new DuplicateResourceException(
                    "Attribute value '" + normalizedValue + "' already exists for attribute '" + attribute.getName() + "'."
            );
        }
    }

    private void updateRawValueIfPresent(AttributeValue av,
                                         Attribute attribute,
                                         AttributeDataType type,
                                         AttributeValueUpdateDTO dto) throws BadRequestException {

        if (dto.getRawValue() == null) {
            return;
        }

        String newNormalizedValue = normalizeRawValue(dto.getRawValue());
        assertUniqueRawValueForUpdate(newNormalizedValue, attribute, type, av.getId());

        applyRawValue(av, type, newNormalizedValue);
        av.setSlug(TextUtils.toSlug(newNormalizedValue));
    }

    private void updateSortOrderIfChanged(
            AttributeValue av,
            Attribute attribute,
            AttributeValueUpdateDTO dto) {

        Integer currentSortOrder = av.getSortOrder();
        Integer requestedSortOrder = dto.getSortOrder();

        if (requestedSortOrder == null || requestedSortOrder.equals(currentSortOrder)) {
            return;
        }

        int newSortOrder = resolveSortOrder(attribute, requestedSortOrder);
        av.setSortOrder(newSortOrder);
    }


    /**
     * Resolves the final sort-order for a new or moved value within a given attribute.
     * <p>
     * If a valid explicit sort-order is requested (within [0, max]), existing
     * values with sort-order {@code >= requestedSortOrder} are shifted up by one
     * and the requested position is used. Otherwise, the value is appended at the
     * end (max + 1).
     *
     * @param attribute          the owning {@link Attribute}
     * @param requestedSortOrder the desired sort-order (may be {@code null})
     * @return the sort-order actually applied
     */
    private int resolveSortOrder(Attribute attribute, Integer requestedSortOrder) {

        int maxSortOrder = repo.findMaxSortOrderByAttribute(attribute).orElse(-1);
        int nextSortOrder = maxSortOrder + 1;

        boolean validSortOrderRequest =
                requestedSortOrder != null &&
                        requestedSortOrder >= 0 &&
                        requestedSortOrder <= maxSortOrder;

        if (validSortOrderRequest) {
            adjustSortOrdersForInsert(attribute, requestedSortOrder);
            return requestedSortOrder;
        } else {
            return nextSortOrder;
        }
    }

    /**
     * Shifts all {@link AttributeValue} instances for the given attribute with
     * {@code sortOrder >= fromSortOrder} up by one to make room for a new
     * value at {@code fromSortOrder}.
     */
    private void adjustSortOrdersForInsert(Attribute attribute, int fromSortOrder) {
        List<AttributeValue> valuesToAdjust = repo.findByAttributeAndSortOrderGreaterThanEqual(attribute, fromSortOrder);
        valuesToAdjust.forEach(av -> av.setSortOrder(av.getSortOrder() + 1));
    }
}
