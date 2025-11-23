package com.makibeans.category.repository;

import com.makibeans.category.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for the category entity.
 */

public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Returns the category with the given name and parent category.
     *
     * @param name The name of the category.
     * @param parentCategory The parent category.
     * @return The category with the given name and parent category.
     */

    boolean existsByNameAndParentCategory(String name, Category parentCategory);

    boolean existsByParentCategoryId(Long parentCategoryId);

    /**
     * Returns the list of categories with the given parent category ID.
     *
     * @param parentCategoryId The ID of the parent category.
     * @return The list of categories with the given parent category ID.
     */

    List<Category> findByParentCategoryId(Long parentCategoryId);

    Page<Category> findAll(Specification<Category> spec, Pageable pageable);


}
