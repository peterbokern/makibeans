package com.makibeans.attribute.service;

import com.makibeans.attribute.dto.AttributeRequestDTO;
import com.makibeans.attribute.dto.AttributeUpdateDTO;
import com.makibeans.attribute.dto.AttributeUsageDTO;
import com.makibeans.attribute.filter.AttributeAdminFilter;
import com.makibeans.attribute.model.Attribute;
import com.makibeans.search.SearchRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface AttributeService {

    Attribute getById(Long id);

    @Transactional(readOnly = true)
    Attribute getByIdIncludingDeleted(Long id);

    @Transactional(readOnly = true)
    Page<Attribute> searchAdmin(SearchRequest<AttributeAdminFilter> req);

    @Transactional(readOnly = true)
    <F> Page<Attribute> search(SearchRequest<F> req, Class<F> filterClass);

    Attribute create(AttributeRequestDTO request);
    Attribute update(Long id, AttributeUpdateDTO request);
    void delete(Long id) throws BadRequestException;
    Attribute restore(Long id) throws BadRequestException;
    AttributeUsageDTO summarizeAttributeUsage(Long attributeId);

    Attribute getBySlugIncludingDeleted(String slug);
}
