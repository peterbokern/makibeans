package com.makibeans.service;
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
    public T create(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        logger.info("Creating new {}: {}", entity.getClass().getSimpleName(), entity);
        return repository.save(entity);
    }

    @Override
    public T update(ID id, T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        logger.info("Updating entity {}: {}", entity.getClass().getSimpleName(), entity);
        return repository.save(entity);
    }

    @Override
    public void delete(ID id) {
        T entity = findById(id);
        logger.info("Deleting {}: {}", entity.getClass().getSimpleName(), entity);
        repository.delete(entity);
    }

    @Override
    public T findById(ID id) {
        logger.info("Looking for entity with id: {}", id);
        T entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entity with id " + id + " not found"));
        logger.info("Found entity: {}", entity);
        return entity;
    }

    @Override
    public List<T> findAll() {
        logger.info("Finding all entities");
        List<T> entities = repository.findAll();
        logger.info("Found {} entities:", entities.size());
        entities.forEach(entity -> logger.info("{}", entity));
        return entities;
    }
}
