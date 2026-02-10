package com.makibeans.category.repository;

import com.makibeans.category.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for the category entity.
 */

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findAll(Specification<Category> spec, Pageable pageable);

    Boolean existsByParentCategoryAndDeletedFalse(Category parentCategory);

    @EntityGraph(attributePaths = {"subCategories"})
    List<Category> findByParentCategoryIsNullAndDeletedFalse();

    Optional<Category> findByIdAndDeletedFalse(Long id);

    Optional<Category> findByParentCategoryIsNullAndSlugIgnoreCase(String slug);

    Optional<Category> findByParentCategoryIsNullAndSlugIgnoreCaseAndIdNot(String slug, Long excludeId);

    Optional<Category> findByParentCategoryAndSlugIgnoreCase(Category parentCategory, String slug);

    Optional<Category> findByParentCategoryAndSlugIgnoreCaseAndIdNot(Category parentCategory, String slug, Long excludeId);

    Optional<Category> findBySlugAndDeletedFalse(String slug);

    Optional<Category> findBySlug(String slug);
}
