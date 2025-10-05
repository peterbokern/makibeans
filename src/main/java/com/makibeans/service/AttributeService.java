package com.makibeans.service;

import com.makibeans.dto.attribute.AttributeRequestDTO;
import com.makibeans.dto.attribute.AttributeResponseDTO;
import com.makibeans.dto.attribute.AttributeUpdateDTO;
import com.makibeans.dto.attribute.AttributeUsageResponseDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.filter.SearchFilter;
import com.makibeans.mapper.AttributeMapper;
import com.makibeans.model.Attribute;
import com.makibeans.repository.AttributeRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.makibeans.util.UpdateUtils.normalize;
import static com.makibeans.util.UpdateUtils.shouldUpdate;

/**
 * Service class for managing Attribute entities.
 * Provides methods to perform CRUD operations and search for AttributeTemplates.
 */
@Service
public class AttributeService extends AbstractCrudService<Attribute, Long> {

    private final AttributeMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(AttributeService.class);
    private final AttributeRepository attributeRepository;
    private final AttributeValueService attributeValueService;

    @Autowired
    public AttributeService(
            JpaRepository<Attribute, Long> repository,
            AttributeMapper mapper, AttributeRepository attributeRepository, @Lazy AttributeValueService attributeValueService) {
        super(repository);
        this.mapper = mapper;
        this.attributeRepository = attributeRepository;
        this.attributeValueService = attributeValueService;
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
    public AttributeResponseDTO getAttributeById(Long id) {
        Attribute attribute = findById(id);
        return mapper.toResponseDTO(attribute);
    }

    /**
     * Searches for AttributeTemplates based on the provided filters.
     * The search is performed on the name of the Attribute.
     *
     * @param searchParams the map containing the search params
     * @return a list of AttributeTemplateResponseDTOs representing the matched attribute templates
     */

    @Transactional(readOnly = true)
    public List<AttributeResponseDTO> findBySearchQuery(Map<String, String> searchParams) {
        logger.debug("Searching AttributeTemplates with filters: {}", searchParams);

        Map<String, Function<Attribute, String>> searchFields = Map.of(
                "name", Attribute::getName);

        Map<String, Comparator<Attribute>> sortFields = Map.of(
                "id", Comparator.comparing(Attribute::getId, Comparator.nullsLast(Comparator.naturalOrder())),
                "name", Comparator.comparing(Attribute::getName, String.CASE_INSENSITIVE_ORDER));

        List<Attribute> matchedTemplates = SearchFilter.apply(
                findAll(),
                searchParams,
                searchFields,
                sortFields);

        logger.info("Found {} matching AttributeTemplates", matchedTemplates.size());

        return matchedTemplates.stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public AttributeUsageResponseDTO getAttributeUsage(Long attrinbuteID) {
        findById(attrinbuteID);
        return null;
    }

    /**
     * Creates a new Attribute and refreshed the cache of valid attribute keys.
     *
     * @param dto the DTO containing the attribute template details
     * @return the created Attribute entity as AttributeTemplateResponseDTO
     * @throws DuplicateResourceException if an Attribute with the same name already exists
     */

    @Transactional
    public AttributeResponseDTO createAttribute(AttributeRequestDTO dto) {
        String normalizedName = normalize(dto.getName());

        validateAttributeTemplateName(normalizedName);

        Attribute attribute = new Attribute(normalizedName);

        Attribute createdAttribute = create(attribute);

        refreshAttributeCache();

        return mapper.toResponseDTO(createdAttribute);
    }

    /**
     * Deletes an Attribute and associated product attributes by ID.
     *
     * @param id the ID of the attribute template to delete
     * @throws ResourceNotFoundException if the attribute template does not exist
     */

    @Transactional
    public void softDeleteAttribute(Long id) {
        findById(id);
        softDeleteAttributeValuesByAttributeId(id);
        //TODO also soft delete product attributes
        softDelete(id);
    }

    /**
     * Soft deletes all AttributeValues associated with the given Attribute ID.
     *
     * @param AttributeId the ID of the Attribute whose values are to be soft deleted
     */
    @Transactional
    public void softDeleteAttributeValuesByAttributeId(Long AttributeId) {
        attributeValueService.softDeleteByAttributeId(AttributeId);
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
    public AttributeResponseDTO updateAttribute(Long id, @Valid AttributeUpdateDTO dto) {

        Attribute attribute = findById(id);

        logger.info("Updating Attribute with ID {}: {}. Trying to change name from '{}' to {}.", id, attribute.getName(), attribute.getName(), dto.getName() == null ? null : dto.getName());

        boolean updated = updateAttributeTemplateNameField(attribute, dto.getName());

        Attribute updatedAttribute = updated ? update(id, attribute) : attribute;

        refreshAttributeCache();

        return mapper.toResponseDTO(updatedAttribute);
    }

    /**
     * Updates the name of the given Attribute if the new name is different from the current name.
     *
     * @param attribute the Attribute to update
     * @param newName           the new name to set
     * @return true if the name was updated, false otherwise
     * @throws DuplicateResourceException if an Attribute with the new name already exists
     */

    private boolean updateAttributeTemplateNameField(Attribute attribute, String newName) {
        String normalizedName = normalize(newName);
        if (shouldUpdate(normalizedName, attribute.getName())) {
            validateAttributeTemplateName(normalizedName);
            attribute.setName(normalizedName);
            return true;
        }
        return false;
    }

    /**
     * Validates the uniqueness of the attribute template name.
     *
     * @param name the name of the attribute template to validate
     * @throws DuplicateResourceException if an attribute template with the same name already exists
     */

    private void validateAttributeTemplateName(String name) {
        if (attributeRepository.existsByName(name)) {
            throw new DuplicateResourceException(
                    String.format("Attribute template with name '%s' already exists.", name));
        }
    }

    /**
     * Retrieves a set of valid attribute keys.
     * The attribute keys are derived from the names of all attribute templates.
     * The result is cached to improve performance.
     *
     * @return a set of valid attribute keys in lowercase.
     */

    @Transactional(readOnly = true)
    @Cacheable("validAttributeKeys")
    public Set<String> getValidAttributeKeys() {
        return findAll().stream()
                .map(template -> template.getName().toLowerCase())
                .collect(Collectors.toSet());
    }

    /**
     * Evicts all entries from the cache named "validAttributeKeys".
     * This method is used to refresh the cache when attribute templates are created, updated, or deleted.
     */

    @CacheEvict(value = "validAttributeKeys", allEntries = true)
    public void refreshAttributeCache() {
    }
}
