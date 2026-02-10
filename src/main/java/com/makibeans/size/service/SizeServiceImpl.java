package com.makibeans.size.service;

import com.makibeans.productvariant.repository.ProductVariantRepository;
import com.makibeans.size.dto.SizeRequestDTO;
import com.makibeans.size.dto.SizeUpdateDTO;
import com.makibeans.size.dto.SizeUsageDTO;
import com.makibeans.size.filter.SizeAdminFilter;
import com.makibeans.size.filter.SizePublicFilter;
import com.makibeans.web.exceptions.BadRequestException;
import com.makibeans.web.exceptions.DuplicateResourceException;
import com.makibeans.size.mapper.SizeMapper;
import com.makibeans.size.model.Size;
import com.makibeans.size.repository.SizeRepository;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import com.makibeans.web.exceptions.ResourceNotFoundException;
import com.makibeans.common.util.TextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SizeServiceImpl implements SizeService {

    private final SizeRepository repo;
    private final SizeMapper mapper;
    private final ProductVariantRepository productVariantRepository;


    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public Size getById(Long id) {
        return repo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Size with id '" + id + "' not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public Size getByIdIncludingDeleted(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Size with id '" + id + "' not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Size> searchPublic(SearchRequest<SizePublicFilter> request) {
        return search(request, SizePublicFilter.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Size> searchAdmin(SearchRequest<SizeAdminFilter> req) {
        return search(req, SizeAdminFilter.class);
    }

    @Override
    @Transactional(readOnly = true)
    public <F> Page<Size> search(SearchRequest<F> req, Class<F> filterClass) {
        Specification<Size> spec = SpecificationFactory.fromRequest(req, filterClass);

        Sort sort = new SortResolver(filterClass)
                .resolve(req.getSortBy(), req.getSortDirection());

        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 20,
                sort
        );

        return repo.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public SizeUsageDTO summarizeProductVariantUsage(Size size) {
        boolean usedInVariants = productVariantRepository.existsBySizeAndDeletedFalse(size);
        return new SizeUsageDTO(usedInVariants);
    }

    // -------------------------------------------------------------------------
    // CREATE / UPDATE
    // -------------------------------------------------------------------------
    @Override
    @Transactional
    public Size create(SizeRequestDTO dto) {
        String trimmedName = dto.getName().trim();

        String normalized = TextUtils.normalizeText(trimmedName);
        String slug = TextUtils.toSlug(normalized);

        validateUniqueSlug(slug, null);

        Size size = new Size();
        size.setName(normalized); // setName keeps slug in sync
        size.setSlug(slug);

        return repo.save(size);
    }

    @Override
    @Transactional
    public Size update(Long id, SizeUpdateDTO dto) {
        Size size = getById(id);

        // if name changed
        if (dto.getName() != null && !dto.getName().trim().equals(size.getName())) {
            String newTrimmedName = dto.getName().trim();
            String normalizedName = TextUtils.normalizeText(newTrimmedName);
            String newSlug = TextUtils.toSlug(normalizedName);
            validateUniqueSlug(newSlug, id);
        }

        mapper.updateEntityFromDTO(dto, size);

        return size;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Size size = getById(id);
        assertNotInUse(size);
        size.setDeleted(true);
    }

    @Override
    @Transactional
    public Size restore(Long id) {
        Size size = getByIdIncludingDeleted(id);
        assertDeleted(size);
        validateUniqueSlug(size.getSlug(), id);
        size.setDeleted(false);
        return size;
    }

    // -------------------------------------------------------------------------
    // VALIDATIONS
    // -------------------------------------------------------------------------

    private void validateUniqueSlug(String slug, Long excludeId) {
        if (slug == null) return;
        boolean exists = (excludeId == null)
                ? repo.existsBySlugAndDeletedFalse(slug)
                : repo.existsBySlugAndIdNotAndDeletedFalse(slug, excludeId);
        if (exists) {
            throw new DuplicateResourceException("Size with slug '" + slug + "' already exists.");
        }
    }

    @Transactional(readOnly = true)
    protected void assertDeleted(Size size) {
        if (size == null) throw new IllegalArgumentException("Size cannot be null.");
        if (!size.isDeleted()) {
            throw new BadRequestException(String.format(
                    "Size '%s' (id: %d) is not deleted and cannot be restored.",
                    size.getName(),
                    size.getId()
            ));
        }
    }

    @Transactional(readOnly = true)
    protected void assertNotInUse(Size size) {
        if (size == null) throw new IllegalArgumentException("Size cannot be null.");

        SizeUsageDTO usage = summarizeProductVariantUsage(size);
        if (usage.isInUse()) {
            throw new BadRequestException(String.format(
                    "Unable to delete Size '%s' (id: %d) because it is in use by one or more Product Variants.",
                    size.getName(),
                    size.getId()
            ));
        }
    }
}
