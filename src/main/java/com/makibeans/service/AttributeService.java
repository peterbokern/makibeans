package com.makibeans.service;

import com.makibeans.dto.attribute.AttributeTemplateRequestDTO;
import com.makibeans.dto.attribute.AttributeTemplateResponseDTO;
import com.makibeans.dto.attribute.AttributeTemplateUpdateDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.filter.SearchFilter;
import com.makibeans.mapper.AttributeMapper;
import com.makibeans.model.Attribute;
import com.makibeans.repository.AttributeTemplateRepository;
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
public class AttributeTemplateService extends AbstractCrudService<Attribute, Long> {

    private final AttributeTemplateRepository attributeTemplateRepository;
    private final AttributeMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(AttributeTemplateService.class);
    private final ProductAttributeService productAttributeService;

    @Autowired
    public AttributeTemplateService(
            JpaRepository<Attribute, Long> repository,
            AttributeTemplateRepository attributeTemplateRepository,
            AttributeMapper mapper, @Lazy ProductAttributeService productAttributeService) {
        super(repository);
        this.attributeTemplateRepository = attributeTemplateRepository;
        this.mapper = mapper;
        this.productAttributeService = productAttributeService;
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
    public AttributeTemplateResponseDTO getAttributeTemplateById(Long id) {
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
    public List<AttributeTemplateResponseDTO> findBySearchQuery(Map<String, String> searchParams) {
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

    /**
     * Creates a new Attribute and refreshed the cache of valid attribute keys.
     *
     * @param dto the DTO containing the attribute template details
     * @return the created Attribute entity as AttributeTemplateResponseDTO
     * @throws DuplicateResourceException if an Attribute with the same name already exists
     */

    @Transactional
    public AttributeTemplateResponseDTO createAttributeTemplate(AttributeTemplateRequestDTO dto) {
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
    public void deleteAttributeTemplate(Long id) {
        findById(id);
        deleteProductAttributesByTemplateId(id);
        delete(id);
    }

    /**
     * Deletes all product attributes associated with the given attribute template ID.
     *
     * @param id the ID of the attribute template whose product attributes are to be deleted
     */

    private void deleteProductAttributesByTemplateId(Long id) {
        productAttributeService.getProductAttributesByTemplateId(id)
                .forEach(productAttribute ->
                        productAttributeService.deleteProductAttribute(productAttribute.getId()));
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
    public AttributeTemplateResponseDTO updateAttributeTemplate(Long id, AttributeTemplateUpdateDTO dto) {

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
        if (attributeTemplateRepository.existsByName(name)) {
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
