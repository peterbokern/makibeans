package com.makibeans.service.service;

import com.makibeans.dto.size.SizeRequestDTO;
import com.makibeans.dto.size.SizeUpdateDTO;
import com.makibeans.model.Size;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.SizeFilter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface SizeService extends CrudService<Size, Long> {

    Size getById(Long id);

    Page<Size> search(SearchRequest<SizeFilter> request);

    Size create(@Valid SizeRequestDTO dto);

    Size update(Long id, @Valid SizeUpdateDTO dto);

    boolean existsByName(String name);
}
