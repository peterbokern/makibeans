package com.makibeans.attribute.attributevalue.repository;

import com.makibeans.attribute.attribute.model.Attribute;
import com.makibeans.attribute.attributevalue.model.AttributeValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository for the attribute value entity.
 */

public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {

    Optional<AttributeValue> findByIdAndDeletedFalse(Long id);

    @EntityGraph(attributePaths = {"attribute"})// Eagerly load associated attribute and category improves performance by reducing the number of queries
    Page<AttributeValue> findAll(Specification<AttributeValue> spec, Pageable pageable);

    @Query("SELECT max(av.sortOrder) FROM AttributeValue av WHERE av.attribute = :attribute")
    Optional<Integer> findMaxSortOrderByAttribute(Attribute attribute);

    List<AttributeValue> findByAttributeAndSortOrderGreaterThanEqual(Attribute attribute, int fromSortOrder);

    boolean existsByAttributeAndDeletedFalse(Attribute attribute);

    AttributeValue findBySlugAndAttributeAndDeletedFalse(String slug, Attribute attribute);

    AttributeValue findBySlugAndAttributeAndIdNotAndDeletedFalse(String slug, Attribute attribute, Long excludeId);

    boolean existsByIdAndAttributeIdAndDeletedFalse(Long attributeValueId, Long expectedAttributeId);
}
