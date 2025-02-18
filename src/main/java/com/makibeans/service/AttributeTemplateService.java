package com.makibeans.service;

import com.makibeans.dto.AttributeTemplateDTO;
import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.model.AttributeTemplate;
import com.makibeans.repository.AttributeTemplateRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttributeTemplateService extends AbstractCrudService<AttributeTemplate, Long> {

    private final AttributeTemplateRepository attributeTemplateRepository;

    @Autowired
    public AttributeTemplateService(AttributeTemplateRepository attributeTemplateRepository) {
        super(attributeTemplateRepository);
        this.attributeTemplateRepository = attributeTemplateRepository;
    }

    /**
     * Creates a new AttributeTemplate.
     *
     * @param dto the DTO containing the attribute template details
     * @return the created AttributeTemplate entity
     * @throws DuplicateResourceException if an AttributeTemplate with the same name already exists
     */
    @Transactional
    public AttributeTemplate createAttributeTemplate(AttributeTemplateDTO dto) {
        String normalizedName = dto.getName().trim().toLowerCase();

        if (attributeTemplateRepository.existsByName(normalizedName)) {
            throw new DuplicateResourceException("Attribute template with name '" + normalizedName + "' already exists.");
        }

        return create(new AttributeTemplate(normalizedName));
    }

    /**
     * Deletes an AttributeTemplate by ID.
     *
     * @param id the ID of the attribute template to delete
     * @throws ResourceNotFoundException if the attribute template does not exist
     */
    @Transactional
    public void deleteAttributeTemplate(Long id) {
        delete(id);
    }

    /**
     * Updates an existing AttributeTemplate.
     *
     * @param id  the ID of the attribute template to update
     * @param dto the DTO containing the updated attribute template name
     * @return the updated AttributeTemplate entity
     * @throws ResourceNotFoundException if the attribute template does not exist
     * @throws DuplicateResourceException if another AttributeTemplate already exists with the same name
     */
    @Transactional
    public AttributeTemplate updateAttributeTemplate(Long id, AttributeTemplateDTO dto) {
        String normalizedName = dto.getName().trim().toLowerCase();

        AttributeTemplate attributeTemplate = findById(id);

        if (!attributeTemplate.getName().equalsIgnoreCase(normalizedName)
                && attributeTemplateRepository.existsByName(normalizedName)) {
            throw new DuplicateResourceException("Attribute template with name '" + normalizedName + "' already exists.");
        }

        attributeTemplate.setName(normalizedName);
        return update(id, attributeTemplate);
    }
}
