package com.makibeans.repository;

import com.makibeans.model.Attribute;
import com.makibeans.model.AttributeValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for the attribute value entity.
 */

public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {

    /**
     * Checks if an attribute value with the given value exists for the specified attribute.
     *
     * @param attribute The attribute to check.
     * @param value The value to check for.
     * @return true if an attribute value with the given value exists for the attribute, false otherwise.
     */

    //REMOVED: @Query("SELECT COUNT(av) > 0 FROM AttributeValue av WHERE av.attribute = :attribute AND av.value = :value")
    boolean existsByAttributeAndValue(Attribute attribute, String value);

    /**
     * Returns all attribute values for the given attribute template.
     *
     * @param attribute The attribute template.
     * @return All attribute values for the given attribute template.
     */

    List<AttributeValue> findAllByAttribute(Attribute attribute);

    /**
     * Returns all non-deleted attribute values for the given attribute ID.
     *
     * @param attributeId The ID of the attribute template.
     * @return All non-deleted attribute values for the given attribute ID.
     */
    List<AttributeValue> findByAttributeIdAndDeletedFalse(Long attributeId);

    /**
     * Counts the number of non-deleted attribute values for the given attribute ID.
     *
     * @param AttributeId The ID of the attribute template.
     * @return The number of non-deleted attribute values for the given attribute ID.
     */
    Long countAttributeValuesByAttributeIdAndDeletedIsFalse(Long AttributeId);

    @EntityGraph(attributePaths = {"attribute"})// Eagerly load associated attribute and category improves performance by reducing the number of queries
    Page<AttributeValue> findAll(Specification<AttributeValue> spec, Pageable pageable);

    boolean existsByValueAndAttributeId(String name, Long attributeId);

    boolean existsByValueAndAttributeIdAndIdNot(String name, Long attributeId, Long id);
}
