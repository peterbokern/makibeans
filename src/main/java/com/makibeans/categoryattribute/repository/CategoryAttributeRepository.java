package com.makibeans.categoryattribute.repository;

import com.makibeans.categoryattribute.model.CategoryAttribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Repository interface for managing `CategoryAttribute` entities.
 */


public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, Long> {

    boolean existsByCategoryIdAndAttributeId(Long categoryId, Long attributeId);
    boolean existsByCategoryId(Long categoryId);

    @EntityGraph(attributePaths = {"attribute", "category"})// Eagerly load associated attribute and category improves performance by reducing the number of queries
    Page<CategoryAttribute> findAll(Specification<CategoryAttribute> spec, Pageable pageable);
}
