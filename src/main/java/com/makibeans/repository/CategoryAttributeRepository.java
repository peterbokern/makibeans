package com.makibeans.repository;

import com.makibeans.mapper.UserMapper;
import com.makibeans.model.CategoryAttribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing `CategoryAttribute` entities.
 */


public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, Long> {

    boolean existsByCategoryIdAndAttributeId(Long categoryId, Long attributeId);

    Optional<CategoryAttribute> findByCategoryIdAndAttributeId(Long categoryId, Long attributeId);

    List<CategoryAttribute> findAllByAttributeId(Long attributeId);

    List<CategoryAttribute> findAllByCategoryId(Long categoryId);


    /**
     * Counts the number of CategoryAttribute entities associated with a specific attribute ID.
     *
     * @param attributeId the ID of the attribute
     * @return the count of CategoryAttribute entities associated with the given attribute ID
     */
    Long countByAttributeId(Long attributeId);

    /**
     * Finds the maximum sort order value among CategoryAttribute entities for a specific category ID.
     *
     * @param categoryId the ID of the category
     * @return the maximum sort order value, or null if no entities exist for the given category ID
     */
    Integer findMaxSortOrderByCategoryId(Long categoryId);

    /**
     * Finds all CategoryAttribute entities matching the given specification with pagination support.
     *
     * @param spec     the specification to filter the entities
     * @param pageable the pagination information
     * @return a paginated list of CategoryAttribute entities matching the specification
     */

    @EntityGraph(attributePaths = {"attribute", "category"})// Eagerly load associated attribute and category improves performance by reducing the number of queries
    Page<CategoryAttribute> findAll(Specification<CategoryAttribute> spec, Pageable pageable);
}
