package com.makibeans.service;

import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.model.AttributeTemplate;
import com.makibeans.model.AttributeValue;
import com.makibeans.repository.AttributeValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AttributeValueService extends AbstractCrudService<AttributeValue, Long> {

    private final AttributeValueRepository attributeValueRepository;
    private final AttributeTemplateService attributeTemplateService;

    @Autowired
    public AttributeValueService(JpaRepository<AttributeValue, Long> repository, AttributeValueRepository attributeValueRepository, AttributeTemplateService attributeTemplateService) {
        super(repository);
        this.attributeValueRepository = attributeValueRepository;
        this.attributeTemplateService = attributeTemplateService;
    }

    /**
     * Creates a new Attribute Value.
     *
     * @param id the ID of the AttributeTemplate
     * @param value the value to be associated with the AttributeTemplate
     * @return the created AttributeValue
     * @throws IllegalArgumentException if id or value are invalid
     * @throws DuplicateResourceException if the value already exists
     */

    @Transactional
    public AttributeValue createAttributeValue(Long id, String value) {

        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty");
        }

        //Throws resourceNotFound
        AttributeTemplate attributeTemplate = attributeTemplateService.findById(id);

        if (attributeValueRepository.existsByValue(attributeTemplate, value.trim())) {
            throw new DuplicateResourceException("Attribute Value " + value + " already exists.");
        }

        AttributeValue attributeValue = new AttributeValue(attributeTemplate, value.trim());
        return create(attributeValue);
    }

    /**
     * Deletes an Attribute Value by ID.
     *
     * @param id the ID of the AttributeValue to delete
     * @throws ResourceNotFoundException if the AttributeValue with the given ID is not found
     */

    @Transactional
    public void deleteAttributeValue(Long id) {
        delete(id);
    }

    /**
     * Updates an existing Attribute Value.
     *
     * @param id the ID of the AttributeValue to update
     * @param value the new value for the AttributeValue
     * @return the updated AttributeValue
     * @throws IllegalArgumentException if id or value are invalid
     */

    @Transactional
    public AttributeValue updateAttributeValue(Long id, String value) {

        if (id == null) {throw new IllegalArgumentException("ID cannot be null.");}

        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty");
        }

        AttributeValue attributeValue = findById(id);

        //
        if (Objects.equals(attributeValue.getValue(), value)) {
            return attributeValue;
        }

        attributeValue.setValue(value);

        return update(id, attributeValue);
    }
}
