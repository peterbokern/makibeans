package com.makibeans.attribute.attribute.service;

import com.makibeans.attribute.attribute.dto.AttributeRequestDTO;
import com.makibeans.attribute.attribute.dto.AttributeUpdateDTO;
import com.makibeans.attribute.attribute.model.Attribute;
import com.makibeans.search.SearchRequest;
import com.makibeans.attribute.attribute.filter.AttributeFilter;
import com.makibeans.common.service.CrudService;
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
