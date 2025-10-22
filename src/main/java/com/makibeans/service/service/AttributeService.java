package com.makibeans.service.service;

import com.makibeans.dto.attribute.AttributeRequestDTO;
import com.makibeans.dto.attribute.AttributeResponseDTO;
import com.makibeans.dto.attribute.AttributeUpdateDTO;
import com.makibeans.model.Attribute;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.AttributeFilter;
import org.springframework.data.domain.Page;

/**
 * Domain API for Attributes.
 * Note: extends CrudService<Attribute, Long> to inherit delete/restore/getOrThrow.
 */
public interface AttributeService extends CrudService<Attribute, Long> {

    AttributeResponseDTO getById(Long id);
    Page<AttributeResponseDTO> search(SearchRequest<AttributeFilter> request);
    AttributeResponseDTO create(AttributeRequestDTO request);
    AttributeResponseDTO update(Long id, AttributeUpdateDTO request);

}
