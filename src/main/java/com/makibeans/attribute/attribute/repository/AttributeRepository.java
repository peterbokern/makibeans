package com.makibeans.attribute.attribute.repository;

import com.makibeans.attribute.attribute.model.Attribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for the attribute template entity.
 */

public interface AttributeRepository extends JpaRepository<Attribute, Long> {

    /**
     * Returns the attribute template with the given name.
     *
     * @param name The name of the attribute template.
     * @return The attribute template with the given name.
     */

    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    Optional<Attribute> findBySlugAndDeletedFalse(String slug);
    Optional<Attribute> findBySlugAndIdNotAndDeletedFalse(String slug, Long id);

    @EntityGraph// Eagerly load associated attribute and category improves performance by reducing the number of queries
    Page<Attribute> findAll(Specification<Attribute> spec, Pageable pageable);
}
