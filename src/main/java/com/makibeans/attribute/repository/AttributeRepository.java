package com.makibeans.attribute.repository;

import com.makibeans.attribute.model.Attribute;
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

    Optional<Attribute> findByIdAndDeletedFalse(Long id);
    Optional<Attribute> findBySlugAndDeletedFalse(String slug);
    Optional<Attribute> findBySlugAndIdNotAndDeletedFalse(String slug, Long id);

    @EntityGraph// Eagerly load associated attribute and category improves performance by reducing the number of queries
    Page<Attribute> findAll(Specification<Attribute> spec, Pageable pageable);

    Optional<Attribute> findBySlug(String slug);
}
