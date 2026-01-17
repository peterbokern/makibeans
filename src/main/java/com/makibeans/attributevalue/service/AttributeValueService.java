package com.makibeans.attributevalue.service;

import com.makibeans.attributevalue.dto.AttributeValueUpdateDTO;
import com.makibeans.attributevalue.dto.AttributeValueUsageDTO;
import com.makibeans.attributevalue.filter.AttributeValueAdminFilter;
import com.makibeans.attributevalue.model.AttributeValue;
import com.makibeans.attributevalue.dto.AttributeValueRequestDTO;
import com.makibeans.search.SearchRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface AttributeValueService {

    @Transactional
    AttributeValue getByIdIncludingDeleted(Long id);

    AttributeValue getById(Long id);

    @Transactional(readOnly = true)
    Page<AttributeValue> searchAdmin(SearchRequest<AttributeValueAdminFilter> req);

    @Transactional(readOnly = true)
    <F> Page<AttributeValue> search(SearchRequest<F> req, Class<F> filterClass);

    AttributeValue create(AttributeValueRequestDTO dto) throws BadRequestException;

    AttributeValue update(Long id, AttributeValueUpdateDTO dto) throws BadRequestException;

    void delete(Long id);

    AttributeValue restore(Long id) throws BadRequestException;

    AttributeValueUsageDTO summarizeAttributeValueUsage(Long attributeValueId);

    AttributeValue getBySlugIncludingDeleted(String slug);
}
