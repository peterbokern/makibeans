package com.makibeans.attribute.attributevalue.service;

import com.makibeans.attribute.attributevalue.dto.AttributeValueUpdateDTO;
import com.makibeans.attribute.attributevalue.filter.AttributeValueFilter;
import com.makibeans.attribute.attributevalue.model.AttributeValue;
import com.makibeans.attribute.attributevalue.dto.AttributeValueRequestDTO;
import com.makibeans.search.SearchRequest;
import com.makibeans.common.service.CrudService;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;

public interface AttributeValueService {

    Page<AttributeValue> search(SearchRequest<AttributeValueFilter> request);

    AttributeValue getById(Long id);

    AttributeValue create(AttributeValueRequestDTO dto) throws BadRequestException;

    AttributeValue update(Long id, AttributeValueUpdateDTO dto) throws BadRequestException;

    void delete(Long id);

    AttributeValue restore(Long id) throws BadRequestException;
}
