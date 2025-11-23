package com.makibeans.service.service;

import com.makibeans.dto.attribute.AttributeRequestDTO;
import com.makibeans.dto.attribute.AttributeUpdateDTO;
import com.makibeans.model.Attribute;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.AttributeFilter;
import org.springframework.data.domain.Page;

/**
 * Domain API for Attributes.
 * Note: extends CrudService<Attribute, Long> to inherit delete/restore/getOrThrow.
 * Services now return entities; controllers are responsible for mapping to DTOs.
 */
public interface AttributeService extends CrudService<Attribute, Long> {

    Attribute getById(Long id);
    Page<Attribute> search(SearchRequest<AttributeFilter> request);
    Attribute create(AttributeRequestDTO request);
    Attribute update(Long id, AttributeUpdateDTO request);
}
