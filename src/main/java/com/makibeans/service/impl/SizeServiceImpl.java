package com.makibeans.service.impl;

import com.makibeans.dto.size.SizeRequestDTO;
import com.makibeans.dto.size.SizeResponseDTO;
import com.makibeans.dto.size.SizeUpdateDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.mapper.SizeMapper;
import com.makibeans.model.Size;
import com.makibeans.repository.SizeRepository;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import com.makibeans.search.filters.SizeFilter;
import com.makibeans.service.service.SizeService;
import com.makibeans.util.TextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SizeServiceImpl implements SizeService {

    private final SizeRepository repo;
    private final SizeMapper mapper;

    @Override
    public JpaRepository<Size, Long> repo() {
        return repo;
    }

    @Override
    public String entityName() {return "Size";}

    @Override
    public SizeResponseDTO getById(Long id) {
        return mapper.toResponseDTO(this.getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<SizeResponseDTO> search(SearchRequest<SizeFilter> req) {
        Specification<Size> spec =
                SpecificationFactory.fromRequest(req, SizeFilter.class);

        Sort sort = new SortResolver(SizeFilter.class)
                .resolve(req.getSortBy(), req.getSortDirection());

        Specification<Size> distinctSpec = (root, query, cb) -> {
            Objects.requireNonNull(query, "CriteriaQuery must not be null");
            query.distinct(true);
            return null;
        };

        Specification<Size> finalSpec = (spec == null) ? distinctSpec : spec.and(distinctSpec);

        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 20,
                sort
        );

        return repo.findAll(finalSpec, pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional
    public SizeResponseDTO create(SizeRequestDTO dto) {
        Size size = new Size();
        size.setName(dto.getName());
        Size saved = repo.save(size);
        return mapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public SizeResponseDTO update(Long id, SizeUpdateDTO dto) {
        Size size = getOrThrow(id);
        mapper.updateEntityFromDTO(dto, size);
        return mapper.toResponseDTO(size);
    }

    @Override
    public boolean existsByName(String name) {
        return repo.existsByNameIgnoreCase(name);
    }

}
