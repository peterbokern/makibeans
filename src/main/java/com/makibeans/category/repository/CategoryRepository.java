package com.makibeans.category.repository;

import com.makibeans.category.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for the category entity.
 */

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findAll(Specification<Category> spec, Pageable pageable);

    Boolean existsByParentCategoryAndDeletedFalse(Category parentCategory);

    boolean existsByParentCategoryAndSlugIgnoreCaseAndDeletedFalse(Category parentCategory, String slug);

    boolean existsByParentCategoryAndSlugIgnoreCaseAndIdNotAndDeletedFalse(Category parentCategory, String slug, Long id);

    boolean existsByParentCategoryIsNullAndSlugIgnoreCaseAndDeletedFalse(String slug);

    boolean existsByParentCategoryIsNullAndSlugIgnoreCaseAndIdNotAndDeletedFalse(String slug, Long id);

    List<Category> findByParentCategoryIsNullAndDeletedFalse();

    Optional<Category> findByIdAndDeletedFalse(Long id);

}
