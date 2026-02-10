package com.makibeans.size.repository;

import com.makibeans.size.model.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing `Size` entities.
 * Provides methods to perform CRUD operations and custom queries on Size data.
 */

public interface SizeRepository extends JpaRepository<Size, Long> {

    /**
     * Checks if a Size with the given name exists (case-insensitive).
     */

    Page<Size> findAll(Specification<Size> spec, Pageable pageable);

    Optional<Size> findByIdAndDeletedFalse(Long id);

    boolean existsBySlugAndDeletedFalse(String slug);

    boolean existsBySlugAndIdNotAndDeletedFalse(String slug, Long id);

}
