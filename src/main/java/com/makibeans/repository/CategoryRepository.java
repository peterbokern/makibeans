package com.makibeans.repository;

import com.makibeans.model.Category;
import com.makibeans.model.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameAndParentCategory(String name, Category parentCategory);

    @EntityGraph(attributePaths = {"products"})
    @Query("select c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id")
    Collection<? extends Product> findWithProductsById(@Param("id") Long id);

    List<Category> findByParentCategoryId(Long parentCategoryId);
}
