package com.makibeans.service.service;

import com.makibeans.dto.attributevalue.AttributeValueRequestDTO;
import com.makibeans.dto.attributevalue.AttributeValueUpdateDTO;
import com.makibeans.model.AttributeValue;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.AttributeValueFilter;
import org.springframework.data.domain.Page;

public interface AttributeValueService extends CrudService<AttributeValue, Long> {

    Page<AttributeValue> search(SearchRequest<AttributeValueFilter> request);

    AttributeValue getById(Long id);

    AttributeValue create(AttributeValueRequestDTO dto);

    AttributeValue update(Long id, AttributeValueUpdateDTO dto);

    void delete(Long id);
}
