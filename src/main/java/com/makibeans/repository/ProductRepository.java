package com.makibeans.repository;

import com.makibeans.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByProductName(String productName);

    Product findByProductName(String productName);
}
