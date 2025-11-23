package com.makibeans.attributevalue.service;

import com.makibeans.attributevalue.dto.AttributeValueRequestDTO;
import com.makibeans.attributevalue.dto.AttributeValueUpdateDTO;
import com.makibeans.attributevalue.model.AttributeValue;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.AttributeValueFilter;
import com.makibeans.common.service.CrudService;
import org.springframework.data.domain.Page;

public interface AttributeValueService extends CrudService<AttributeValue, Long> {

    Page<AttributeValue> search(SearchRequest<AttributeValueFilter> request);

    AttributeValue getById(Long id);

    AttributeValue create(AttributeValueRequestDTO dto);

    AttributeValue update(Long id, AttributeValueUpdateDTO dto);

    void delete(Long id);
}
