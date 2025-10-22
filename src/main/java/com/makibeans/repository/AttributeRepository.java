package com.makibeans.repository;

import com.makibeans.model.Attribute;
import com.makibeans.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the attribute template entity.
 */

public interface AttributeRepository extends JpaRepository<Attribute, Long> {

    /**
     * Returns the attribute template with the given name.
     *
     * @param trimmedName The name of the attribute template.
     * @return The attribute template with the given name.
     */

    boolean existsByName(String trimmedName);
    boolean existsByNameAndIdNot(String name, Long id);

    @EntityGraph// Eagerly load associated attribute and category improves performance by reducing the number of queries
    Page<Attribute> findAll(Specification<Attribute> spec, Pageable pageable);
}
