package com.makibeans.repository;

import com.makibeans.model.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

    boolean existsByProductIdAndAttributeTemplateId(Long productId, Long templateId);
}
