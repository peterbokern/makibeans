package com.makibeans.service;
import com.makibeans.exeptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class AbstractCrudService<T, ID> implements CrudService<T, ID> {
    protected final JpaRepository<T, ID> repository;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AbstractCrudService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public T create(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException(getEntityName() + " cannot be null");
        }
        logger.info("Creating new {}: {}", entity.getClass().getSimpleName(), entity);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public T update(ID id, T entity) {
        if (entity == null) {
            throw new IllegalArgumentException(getEntityName() + " cannot be null");
        }
        logger.info("Updating entity {}: {}", getEntityName(), entity);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(ID id) {
        T entity = findById(id);
        logger.info("Deleting {}: {}", getEntityName(), entity);
        repository.delete(entity);
    }

    @Override
    public T findById(ID id) {
        logger.info("Looking for entity with id: {}", id);
        T entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getEntityName() + " with id " + id + " not found"));
        logger.info("Found {}: {}", getEntityName(), entity);
        return entity;
    }

    @Override
    public List<T> findAll() {
        List<T> entities = repository.findAll();
        logger.info("Found {} entities of {}:", entities.size(), getEntityName());
        entities.forEach(entity -> logger.info("{}", entity));
        return entities;
    }

    //Get entity name e.g. AttributeTemplate
    private String getEntityName() {
        return getClass().getSimpleName().replace("Service", "");
    }
}
