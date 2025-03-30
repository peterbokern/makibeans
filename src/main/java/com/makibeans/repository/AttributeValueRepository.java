package com.makibeans.repository;

import com.makibeans.model.AttributeTemplate;
import com.makibeans.model.AttributeValue;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.beans.JavaBean;
import java.util.List;
import java.util.Optional;

/**
 * Repository for the attribute value entity.
 */

public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {

    /**
     * Returns the attribute value with the given value.
     *
     * @param value The value of the attribute value.
     * @return The attribute value with the given value.
     */

    @Query("SELECT COUNT(av) > 0 FROM AttributeValue av WHERE av.attributeTemplate = :attributeTemplate AND av.value = :value")
    boolean existsByValue(AttributeTemplate attributeTemplate, String value);

    /**
     * Returns all attribute values for the given attribute template.
     *
     * @param attributeTemplate The attribute template.
     * @return All attribute values for the given attribute template.
     */

    //@EntityGraph(attributePaths = {"attributeTemplate"}) // Ensures `attributeTemplate` is fetched
    List<AttributeValue> findAllByAttributeTemplate(AttributeTemplate attributeTemplate);

}
