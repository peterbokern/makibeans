package com.makibeans.service;

import com.makibeans.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Generic abstract service class that provides basic CRUD operations.
 * Intended to be extended by specific service classes for entity types.
 *
 * @param <T>  the entity type
 * @param <ID> the type of the entity's identifier
 */

public abstract class AbstractCrudService<T, ID> implements CrudService<T, ID> {

    protected final JpaRepository<T, ID> repository;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AbstractCrudService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    /**
     * Creates a new entity in the database.
     *
     * @param entity the entity to create; must not be null
     * @return the created entity
     * @throws IllegalArgumentException if the entity is null
     */

    @Override
    @Transactional
    public T create(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException(getEntityName() + " cannot be null.");
        }
        logger.info("Creating new {}: {}", getEntityName(), entity);
        return repository.save(entity);
    }

    /**
     * Updates an existing entity.
     *
     * @param id     the ID of the entity to update; must not be null
     * @param entity the updated entity; must not be null
     * @return the updated entity
     * @throws IllegalArgumentException if the ID or entity is null
     */

    @Override
    @Transactional
    public T update(ID id, T entity) {
        if (id == null) {
            throw new IllegalArgumentException(getEntityName() + " ID cannot be null.");
        }
        if (entity == null) {
            throw new IllegalArgumentException(getEntityName() + " cannot be null.");
        }
        logger.info("Updating {} with ID {}: {}", getEntityName(), id, entity);
        return repository.save(entity);
    }

    /**
     * Deletes an entity by ID.
     *
     * @param id the ID of the entity to delete; must not be null
     * @throws IllegalArgumentException  if the ID is null
     * @throws ResourceNotFoundException if the entity does not exist
     */

    @Override
    @Transactional
    public void delete(ID id) {
        if (id == null) {
            throw new IllegalArgumentException(getEntityName() + " ID cannot be null.");
        }
        T entity = findById(id);
        logger.info("Deleting {} with ID {}: {}", getEntityName(), id, entity);
        repository.delete(entity);
    }

    /**
     * Retrieves all entities of this type.
     *
     * @return a list of all entities
     */

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        List<T> entities = repository.findAll();
        logger.info("Retrieved {} {}(s)", entities.size(), getEntityName());
        return entities;
    }

    /**
     * Retrieves an entity by ID.
     *
     * @param id the ID of the entity to retrieve; must not be null
     * @return the found entity
     * @throws IllegalArgumentException  if the ID is null
     * @throws ResourceNotFoundException if no entity is found with the given ID
     */

    @Override
    @Transactional(readOnly = true)
    public T findById(ID id) {
        if (id == null) {
            throw new IllegalArgumentException(getEntityName() + " ID cannot be null.");
        }
        logger.debug("Fetching {} with ID {}", getEntityName(), id);
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getEntityName() + " with ID " + id + " not found."));
    }

    /**
     * Gets a human-readable name of the entity type for logging purposes.
     *
     * @return the entity name (e.g., "AttributeTemplate" from "AttributeTemplateService")
     */

    private String getEntityName() {
        return getClass().getSimpleName().replace("Service", "");
    }
}
