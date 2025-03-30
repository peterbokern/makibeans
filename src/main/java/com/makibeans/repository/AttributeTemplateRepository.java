package com.makibeans.repository;

import com.makibeans.model.AttributeTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for the attribute template entity.
 */

public interface AttributeTemplateRepository extends JpaRepository<AttributeTemplate, Long> {

    /**
     * Returns the attribute template with the given name.
     *
     * @param trimmedName The name of the attribute template.
     * @return The attribute template with the given name.
     */

    boolean existsByName(String trimmedName);
}
