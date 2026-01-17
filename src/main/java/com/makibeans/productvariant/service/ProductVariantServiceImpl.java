package com.makibeans.productvariant.service;

import com.makibeans.audit.model.DeleteReason;
import com.makibeans.productvariant.dto.ProductVariantAdminResponseDTO;
import com.makibeans.productvariant.dto.ProductVariantRequestDTO;
import com.makibeans.productvariant.dto.ProductVariantUpdateDTO;
import com.makibeans.productvariant.filter.ProductVariantAdminFilter;
import com.makibeans.web.exceptions.DuplicateResourceException;
import com.makibeans.productvariant.mapper.ProductVariantMapper;
import com.makibeans.product.model.Product;
import com.makibeans.productvariant.model.ProductVariant;
import com.makibeans.size.service.SizeServiceImpl;
import com.makibeans.size.model.Size;
import com.makibeans.productvariant.repository.ProductVariantRepository;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import com.makibeans.product.service.ProductService;

import com.makibeans.web.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository repo;
    private final ProductVariantMapper mapper;
    private final ProductService productService;
    private final SizeServiceImpl sizeService;

    @Override
    public ProductVariant getById(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("ProductVariant with id " + id + " not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductVariant> searchAdmin(SearchRequest<ProductVariantAdminFilter> req) {
        return search(req, ProductVariantAdminFilter.class);
    }

    //TODO : add default search to all searches

    // -------- Core search implementation --------
    //SORTING: Always apply default sorting by isDefault desc, id asc if not provided by client
    @Override
    @Transactional(readOnly = true)
    public <F> Page<ProductVariant> search(SearchRequest<F> req, Class<F> filterClass) {
        Specification<ProductVariant> spec =
                SpecificationFactory.fromRequest(req, filterClass);

        Sort sort = new SortResolver(filterClass)
                .resolve(req.getSortBy(), req.getSortDirection());

        // Ensure default sorting by isDefault desc, id asc if not provided by client
        sort = resolveSortWithDefault(sort);

        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 20,
                sort
        );

        return repo.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariant> getPublicVariantsByProductId(Long productId) {
        return repo.findByProductIdAndDeletedFalseOrderByIsDefaultDescIdAsc(productId);
    }

    @Override
    @Transactional
    public ProductVariant create(ProductVariantRequestDTO dto) {

        Product product = productService.findById((dto.getProductId()));
        Size size = sizeService.getById(dto.getSizeId());

        assertUniqueProductAndSize(product, size);

        ProductVariant productVariant = ProductVariant.builder()
                .product(product)
                .size(size)
                .priceInCents(dto.getPriceInCents())
                .stock(dto.getStock())
                .sku(generateSkuValue(product, size))
                .build();

        ProductVariant saved = repo.save(productVariant);

        applyDefaultVariantOnCreate(product.getId(), dto.getIsDefault(), saved.getId());

        return getById(saved.getId());
    }

    @Override
    @Transactional
    public ProductVariant update(Long id, ProductVariantUpdateDTO dto) {
        var productVariant = getById(id);
        var product = productVariant.getProduct();

        boolean wasDefault = Boolean.TRUE.equals(productVariant.getIsDefault());

        mapper.updateEntityFromDTO(dto, productVariant);

        //Ensure default variant logic after update
        // If isDefault is set to true, set this variant as default
        // If isDefault is set to false, and it was previously default, set another variant as default
        // If isDefault is null, do nothing
        if (dto.getIsDefault() != null) {

            boolean requestDefault = Boolean.TRUE.equals(dto.getIsDefault());

            if (requestDefault) {
                setDefault(product.getId(), productVariant.getId());
            } else if (wasDefault) {
                productVariant.setIsDefault(false);
                handleDefaultVariantAfterDeletionOrUpdate(product.getId(), productVariant.getId());
            }
            repo.flush();
        }

        return getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ProductVariant existing = getById(id);

        if (existing.isDeleted()) return;

        Long productId = existing.getProduct().getId();
        boolean wasDefault = Boolean.TRUE.equals(existing.getIsDefault());

        existing.setIsDefault(false);
        existing.setDeleted(true);
        existing.setDeletedReason(DeleteReason.ADMIN_DELETED);

        // After deletion, if it was the default variant, set another variant as default
        if (wasDefault) handleDefaultVariantAfterDeletionOrUpdate(productId, existing.getId());
    }

    @Override
    @Transactional
    public void deleteByProductId(Long productId, DeleteReason reason) {
        var variants = repo.findByProductIdAndDeletedFalse(productId);
        variants.forEach(variant -> {
            if (!variant.isDeleted()) {
                variant.setDeleted(true);
                variant.setDeletedReason(reason);
            }
        });
    }

// -------- Convenience ops --------


    //TODO : need to implement
    @Override
    @Transactional
    public ProductVariantAdminResponseDTO setStock(Long variantId, Long stock) {
        var existing = getById(variantId);
        existing.setStock(stock != null ? Math.max(0L, stock) : 0L);
        return mapper.toAdminResponseDTO(repo.save(existing));
    }

    @Override
    @Transactional
    public ProductVariantAdminResponseDTO incrementStock(Long variantId, Long by) {
        var existing = getById(variantId);
        existing.setStock(Math.max(0L, (existing.getStock() == null ? 0L : existing.getStock()) + (by == null ? 0L : by)));
        return mapper.toAdminResponseDTO(repo.save(existing));
    }

    @Override
    @Transactional
    public ProductVariantAdminResponseDTO decrementStock(Long variantId, Long by) {
        var existing = getById(variantId);
        existing.setStock(Math.max(0L, (existing.getStock() == null ? 0L : existing.getStock()) - (by == null ? 0L : by)));
        return mapper.toAdminResponseDTO(repo.save(existing));
    }

    // -------- Helpers --------
    private void assertUniqueProductAndSize(Product product, Size size) {
        if (repo.existsByProductAndSizeAndDeletedFalse(product, size)) {
            throw new DuplicateResourceException(
                    "A ProductVariant with product ID " + product.getId() + " and size ID " + size.getId() + " already exists."
            );
        }
    }

    private String generateSkuValue(Product product, Size size) {
        // Example SKU: PROD-{productId}-SZ-{sizeId}-{8char}
        Long productId = product != null ? product.getId() : null;
        Long sizeId = size != null ? size.getId() : null;
        String rand = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return String.format("PROD-%s-SZ-%s-%s",
                productId == null ? "X" : productId,
                sizeId == null ? "X" : sizeId,
                rand);
    }

    // sets the specified product variant as the default for the given product and unsets any other default variant.
    @Override
    @Transactional
    public void setDefault(Long productId, Long productVariantId) {
        if (productId == null || productVariantId == null) return;
        if (!repo.existsByIdAndProductIdAndDeletedFalse(productVariantId, productId)) {
            throw new ResourceNotFoundException("Unable to set default product variant. ProductVariant with id " + productVariantId + " not found for Product with id " + productId);
        }
        int updated = repo.setDefaultProductVariant(productId, productVariantId);
        if (updated == 0) {
            throw new ResourceNotFoundException("No active variants found for Product with id " + productId);
        }
    }

    // Ensures that a default variant is set for the product.
// If isDefault is true, sets the specified variant as default.
// If no default is set, sets the specified variant as default.
// Throws ResourceNotFoundException if the specified variant does not belong to the product.
    private void applyDefaultVariantOnCreate(Long productId, Boolean isDefault, Long productVariantId) {

        boolean noDefaultSet = !repo.existsByProductIdAndDeletedFalseAndIsDefaultTrue(productId);
        boolean requestDefault = Boolean.TRUE.equals(isDefault);

        if (requestDefault || noDefaultSet) {
            if (!repo.existsByIdAndProductIdAndDeletedFalse(productVariantId, productId)) {
                throw new ResourceNotFoundException("Unable to set default product variant. ProductVariant with id " + productVariantId + " not found for Product with id " + productId);
            }
            setDefault(productId, productVariantId);
        }
    }

    private void handleDefaultVariantAfterDeletionOrUpdate(Long productId, Long excludeVariantId) {
        var next = repo.findFirstByProductIdAndDeletedFalseAndIdNotOrderByIdAsc(productId, excludeVariantId);
        next.ifPresent(v -> setDefault(productId, v.getId()));
    }

    private Sort resolveSortWithDefault(Sort sort) {
        Sort defaultSort = Sort.by(
                Sort.Order.desc("isDefault"),
                Sort.Order.asc("id")
        );

        // Ensure default sorting by isDefault desc, id asc if not provided by client
        if (sort == null || sort.isUnsorted()) {
            sort = defaultSort;

        } else {
            var clientSort = sort.stream()
                    .map(Sort.Order::getProperty)
                    .collect(Collectors.toSet());

            if (!clientSort.contains("isDefault")) {
                sort = sort.and(Sort.by(Sort.Order.desc("isDefault")));
            }

            if (!clientSort.contains("id")) {
                sort = sort.and(Sort.by(Sort.Order.asc("id")));
            }
        }
        return sort;
    }
}