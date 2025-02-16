package com.makibeans.repository;

import com.makibeans.model.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SizeRepository extends JpaRepository<Size, Long> {

    boolean existsByName(String name);
}
