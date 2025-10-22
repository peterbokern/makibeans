package com.makibeans.repository;

import com.makibeans.model.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing `Size` entities.
 * Provides methods to perform CRUD operations and custom queries on Size data.
 */

public interface SizeRepository extends JpaRepository<Size, Long> {

    /**
     * Checks if a Size with the given name exists.
     *
     * @param name the name of the Size
     * @return true if a Size with the given name exists, false otherwise
     */
    boolean existsByNameIgnoreCase(String name);

    Page<Size> findAll(Specification<Size> spec, Pageable pageable);

}
