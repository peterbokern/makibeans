package com.makibeans.size.service;
import com.makibeans.size.dto.SizeRequestDTO;
import com.makibeans.size.dto.SizeUpdateDTO;
import com.makibeans.size.dto.SizeUsageDTO;
import com.makibeans.size.filter.SizeAdminFilter;
import com.makibeans.size.filter.SizePublicFilter;
import com.makibeans.size.model.Size;
import com.makibeans.search.SearchRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface SizeService  {

    Size getById(Long id);

    @Transactional
    Size getByIdIncludingDeleted(Long id);

    @Transactional(readOnly = true)
    Page<Size> searchPublic(SearchRequest<SizePublicFilter> request);

    @Transactional(readOnly = true)
    Page<Size> searchAdmin(SearchRequest<SizeAdminFilter> req);

    <F> Page<Size> search(SearchRequest<F> request, Class<F> filterClass);

    @Transactional(readOnly = true)
    SizeUsageDTO summarizeProductVariantUsage(Size size);

    Size create(@Valid SizeRequestDTO dto);

    Size update(Long id, @Valid SizeUpdateDTO dto);

    @Transactional
    void delete(Long id);

    @Transactional
    Size restore(Long id);
}
