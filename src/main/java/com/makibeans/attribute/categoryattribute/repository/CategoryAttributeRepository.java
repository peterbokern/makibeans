package com.makibeans.attribute.categoryattribute.repository;

import com.makibeans.attribute.attribute.model.Attribute;
import com.makibeans.attribute.categoryattribute.model.CategoryAttribute;
import com.makibeans.category.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Repository interface for managing `CategoryAttribute` entities.
 */


public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, Long> {

    boolean existsByCategoryIdAndAttributeId(Long categoryId, Long attributeId);
    boolean existsByCategoryId(Long categoryId);

    @EntityGraph(attributePaths = {"attribute", "category"})// Eagerly load associated attribute and category improves performance by reducing the number of queries
    Page<CategoryAttribute> findAll(Specification<CategoryAttribute> spec, Pageable pageable);

    boolean existsByAttributeAndDeletedFalse(Attribute attribute);

    boolean existsByCategoryAndAttributeAndDeletedFalse(Category category, Attribute attribute);

    @Query("SELECT max(ca.sortOrder) FROM CategoryAttribute ca WHERE ca.category = :category")
    Optional<Integer> findMaxSortOrderByCategory(Category category);


    Set<CategoryAttribute> findByCategoryIdAndDeletedFalse(Long categoryId);

    List<CategoryAttribute> findByCategoryAndSortOrderGreaterThanEqual(Category category, int fromSortOrder);

    Optional<CategoryAttribute> findByIdAndDeletedFalse(Long id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE CategoryAttribute ca SET ca.deleted = false, ca.deletedBy = null, ca.deletedAt = null WHERE ca.category.id = :categoryId AND ca.deleted = true")
    int restoreByCategoryId(@Param("categoryId") Long categoryId);
}
