package com.makibeans.size.service;

import com.makibeans.common.service.CrudService;
import com.makibeans.size.dto.SizeRequestDTO;
import com.makibeans.size.dto.SizeUpdateDTO;
import com.makibeans.size.model.Size;
import com.makibeans.search.SearchRequest;
import com.makibeans.size.filter.SizeFilter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface SizeService extends CrudService<Size, Long> {

    Size getById(Long id);

    Page<Size> search(SearchRequest<SizeFilter> request);

    Size create(@Valid SizeRequestDTO dto);

    Size update(Long id, @Valid SizeUpdateDTO dto);

    boolean existsByName(String name);
}
