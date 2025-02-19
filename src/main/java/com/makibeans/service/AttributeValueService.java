package com.makibeans.service;

import com.makibeans.dto.AttributeValueDTO;
import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.model.AttributeTemplate;
import com.makibeans.model.AttributeValue;
import com.makibeans.repository.AttributeValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttributeValueService extends AbstractCrudService<AttributeValue, Long> {

    private final AttributeValueRepository attributeValueRepository;
    private final AttributeTemplateService attributeTemplateService;

    @Autowired
    public AttributeValueService(AttributeValueRepository attributeValueRepository,
                                 AttributeTemplateService attributeTemplateService) {
        super(attributeValueRepository);
        this.attributeValueRepository = attributeValueRepository;
        this.attributeTemplateService = attributeTemplateService;
    }

    /**
     * Creates a new AttributeValue.
     *
     * @param dto the DTO containing the attribute value details
     * @return the created AttributeValue entity
     * @throws DuplicateResourceException if an AttributeValue with the same value already exists
     */

    @Transactional
    public AttributeValue createAttributeValue(AttributeValueDTO dto) {
        String normalizedValue = dto.getValue().trim();

        AttributeTemplate attributeTemplate = attributeTemplateService.findById(dto.getTemplateId());

        if (attributeValueRepository.existsByValue(attributeTemplate, normalizedValue)) {
            throw new DuplicateResourceException("Attribute value '" + normalizedValue + "' already exists.");
        }

        AttributeValue attributeValue = new AttributeValue(attributeTemplate, normalizedValue);
        return create(attributeValue);
    }

    /**
     * Deletes an AttributeValue by ID.
     *
     * @param id the ID of the attribute value to delete
     * @throws ResourceNotFoundException if the attribute value does not exist
     */

    @Transactional
    public void deleteAttributeValue(Long id) {
        delete(id);
    }

    /**
     * Updates an existing AttributeValue.
     *
     * @param id  the ID of the attribute value to update
     * @param dto the DTO containing the updated attribute value details
     * @return the updated AttributeValue entity
     * @throws ResourceNotFoundException  if the attribute value does not exist
     * @throws DuplicateResourceException if another AttributeValue with the same value already exists
     */

    @Transactional
    public AttributeValue updateAttributeValue(Long id, AttributeValueDTO dto) {
        String normalizedValue = dto.getValue().trim();

        AttributeValue attributeValue = findById(id);
        AttributeTemplate attributeTemplate = attributeTemplateService.findById(dto.getId());

        if (!attributeValue.getValue().equalsIgnoreCase(normalizedValue) && attributeValueRepository.existsByValue(attributeTemplate, normalizedValue)) {
            throw new DuplicateResourceException("Attribute value '" + normalizedValue + "' already exists for attribute template " + attributeTemplate.getName() + ".");
        }

        attributeValue.setValue(normalizedValue);
        return update(id, attributeValue);
    }
}
