package com.makibeans.repository;

import com.makibeans.model.AttributeTemplate;
import com.makibeans.model.AttributeValue;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.beans.JavaBean;
import java.util.List;
import java.util.Optional;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {
    Optional<AttributeValue> findByValue(String value);

    //returns true if attribute value exists for atttribute template
    @Query("SELECT COUNT(av) > 0 FROM AttributeValue av WHERE av.attributeTemplate = :attributeTemplate AND av.value = :value")
    boolean existsByValue(AttributeTemplate attributeTemplate, String value);

    //@EntityGraph(attributePaths = {"attributeTemplate"}) // Ensures `attributeTemplate` is fetched
    List<AttributeValue> findAllByAttributeTemplate(AttributeTemplate attributeTemplate);

}
