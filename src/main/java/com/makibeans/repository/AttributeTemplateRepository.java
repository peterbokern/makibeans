package com.makibeans.repository;

import com.makibeans.model.AttributeTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttributeTemplateRepository extends JpaRepository<AttributeTemplate, Long> {

    Optional<AttributeTemplate> findByName(String name);

}
