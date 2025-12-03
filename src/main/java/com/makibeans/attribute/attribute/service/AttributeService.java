package com.makibeans.attribute.attribute.service;

import com.makibeans.attribute.attribute.dto.AttributeRequestDTO;
import com.makibeans.attribute.attribute.dto.AttributeUpdateDTO;
import com.makibeans.attribute.attribute.dto.AttributeUsageDTO;
import com.makibeans.attribute.attribute.model.Attribute;
import com.makibeans.search.SearchRequest;
import com.makibeans.attribute.attribute.filter.AttributeFilter;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;

/**
 * Domain API for Attributes.
 * Note: extends CrudService<Attribute, Long> to inherit delete/restore/getOrThrow.
 * Services now return entities; controllers are responsible for mapping to DTOs.
 */
public interface AttributeService {

    Attribute getById(Long id);
    Page<Attribute> search(SearchRequest<AttributeFilter> request);
    Attribute create(AttributeRequestDTO request);
    Attribute update(Long id, AttributeUpdateDTO request);
    void delete(Long id) throws BadRequestException;

    AttributeUsageDTO summarizeAttributeUsage(Long attributeId);

    Attribute restore(Long id) throws BadRequestException;

}
