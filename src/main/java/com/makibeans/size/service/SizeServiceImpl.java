package com.makibeans.size.service;

import com.makibeans.size.dto.SizeRequestDTO;
import com.makibeans.size.dto.SizeUpdateDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.size.mapper.SizeMapper;
import com.makibeans.size.model.Size;
import com.makibeans.size.repository.SizeRepository;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import com.makibeans.size.filter.SizeFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SizeServiceImpl implements SizeService {

    private final SizeRepository repo;
    private final SizeMapper mapper;

    // -------------------------------------------------------------------------
    // CrudService adapter
    // -------------------------------------------------------------------------
    @Override
    public JpaRepository<Size, Long> repo() {
        return repo;
    }

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public Size getById(Long id) {
        return getOrThrow(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Size> search(SearchRequest<SizeFilter> request) {
        Specification<Size> spec = SpecificationFactory.fromRequest(request, SizeFilter.class);

        Sort sort = new SortResolver(SizeFilter.class)
                .resolve(request.getSortBy(), request.getSortDirection());

        Pageable pageable = PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 20,
                sort
        );

        return repo.findAll(spec, pageable);
    }

    // -------------------------------------------------------------------------
    // CREATE / UPDATE
    // -------------------------------------------------------------------------
    @Override
    @Transactional
    public Size create(SizeRequestDTO dto) {
        String name = dto.getName();

        validateUniqueName(name);
        Size size = new Size();
        size.setName(name);

        return repo.save(size);
    }


    @Override
    @Transactional
    public Size update(Long id, SizeUpdateDTO dto) {
        Size size = getOrThrow(id);

        validateUniqueNameAndIdNot(dto.getName(), id);

        mapper.updateEntityFromDTO(dto, size);

        return size;
    }

    private void validateUniqueNameAndIdNot(String name, Long id) {
        if (repo.existsByNameAndIdNot(name, id)) {
            throw new DuplicateResourceException("Size with name '" + name + "' already exists.");
        }
    }

    // -------------------------------------------------------------------------
    // OTHER
    // -------------------------------------------------------------------------
    @Override
    public boolean existsByName(String name) {
        return repo.existsByNameIgnoreCase(name);
    }


    private void validateUniqueName(String name) {
        if (existsByName(name)) {
            throw new DuplicateResourceException("Size with name '" + name + "' already exists.");
        }
    }
}
