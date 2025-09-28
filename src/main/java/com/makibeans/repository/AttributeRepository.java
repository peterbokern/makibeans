package com.makibeans.repository;

import com.makibeans.model.Attribute;
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
}
