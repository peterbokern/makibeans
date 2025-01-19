package com.makibeans.service;

import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.model.AttributeTemplate;
import com.makibeans.repository.AttributeTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

//public abstract class GenericService<T, ID> implements GenericService<T, ID>
@Service
public class AttributeTemplateService {

    //protected final JpaRepository<T, Long> repository;
    private final AttributeTemplateRepository attributeTemplateRepository;

    //private static final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final Logger logger = LoggerFactory.getLogger(AttributeTemplateService.class);

    //public AttributeTemplateService(JpaRepository<T, Long> repository) - Constructor
        //this.repository = repository;
    @Autowired
    public AttributeTemplateService(AttributeTemplateRepository attributeTemplateRepository) {
        this.attributeTemplateRepository = attributeTemplateRepository;
    }

    /**
     * Create a new attribute template
     *
     * @param name the name of the attribute template
     * @return the created attribute template
     * @throws IllegalArgumentException   if the name is null or empty
     * @throws DuplicateResourceException if the attribute template with the same name already exists
     */

    // T create(T entity) - Create a new entity
    //public T create(T entity) - Create a new entity
    @Transactional
    public AttributeTemplate createAttributeTemplate(String name) {

        //validation
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Attribute template name cannot be null or empty");
        }

        // trim input
        final String trimmedName = name.trim();

        //save attribute template if not exists
        try {
            AttributeTemplate attributeTemplate = new AttributeTemplate(trimmedName);
            logger.info("Creating new attribute template: {}", attributeTemplate);
            return attributeTemplateRepository.save(attributeTemplate);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("Attribute template with name " + trimmedName + " already exists");
        }

    }

    /**
     * Delete an attribute template
     *
     * @param id the id of the attribute template to delete
     * @throws ResourceNotFoundException if the attribute template with the id does not exist
     */

    //void deleteById(ID id) - Delete an entity by its id
    //public void delete(ID id)
    @Transactional
    public void deleteAttributeTemplate(Long id) {

        //delete attribute template if exists
        AttributeTemplate attributeTemplate = findAttributeTemplateById(id);
        logger.info("Deleting attribute template: {}", attributeTemplate);
        attributeTemplateRepository.delete(attributeTemplate);
    }

    /**
     * Update an attribute template
     *
     * @param id      the id of the attribute template to update
     * @param newName the new name of the attribute template
     * @return the updated attribute template
     * @throws IllegalArgumentException  if the new name is null or empty
     * @throws ResourceNotFoundException if the attribute template with the id does not exist
     */

    // T update(ID id, T entity)
    //public T update(ID id, T entity)
    @Transactional
    public AttributeTemplate updateAttributeTemplate(Long id, String newName) {

        //input validation

        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("New attribute template name cannot be null or empty");
        }

        // trim input
        final String trimmedNewName = newName.trim();

        //find attribute template
        AttributeTemplate attributeTemplate = findAttributeTemplateById(id);

        if (attributeTemplate.getName().equals(trimmedNewName)) {
            return attributeTemplate;
        }

        final String oldName = attributeTemplate.getName();

        //update attribute template and save
        attributeTemplate.setName(trimmedNewName);
        logger.info("Updating attribute template: {}, changed name from {} to {}", attributeTemplate, oldName, attributeTemplate.getName());
        return attributeTemplateRepository.save(attributeTemplate);

    }

    // T findById(ID id) - Find an entity by its id
    // public T findById(ID id)
    public AttributeTemplate findAttributeTemplateById(Long id) {
        return attributeTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attribute template with id " + id + " does not exist"));
    }

    //List<T> findAll() - Find all entities
    // public List<T> findAll()
    public List<AttributeTemplate> findAllAttributeTemplates() {
        return attributeTemplateRepository.findAll();
    }
}
