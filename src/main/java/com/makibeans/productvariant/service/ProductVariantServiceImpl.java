package com.makibeans.productvariant.service;

import com.makibeans.productvariant.dto.ProductVariantRequestDTO;
import com.makibeans.productvariant.dto.ProductVariantResponseDTO;
import com.makibeans.productvariant.dto.ProductVariantUpdateDTO;
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
import com.makibeans.productvariant.filter.ProductVariantFilter;
import com.makibeans.product.service.ProductService;

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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository repo;
    private final ProductVariantMapper mapper;
    private final ProductService productService;
    private final SizeServiceImpl sizeService;

    @Override
    public JpaRepository<ProductVariant, Long> repo() {
        return repo;
    }

    @Override
    public String entityName() {
        return "ProductVariant";
    }

    @Override
    public ProductVariant getById(Long id) {
        return getOrThrow(id);
    }

    @Transactional(readOnly = true)
    public Page<ProductVariant> search(SearchRequest<ProductVariantFilter> req) {
        Specification<ProductVariant> spec =
                SpecificationFactory.fromRequest(req, ProductVariantFilter.class);

        Sort sort = new SortResolver(ProductVariantFilter.class)
                .resolve(req.getSortBy(), req.getSortDirection());

        Specification<ProductVariant> distinctSpec = (root, query, cb) -> {
            Objects.requireNonNull(query, "CriteriaQuery must not be null");
            query.distinct(true);
            return null;
        };

        Specification<ProductVariant> finalSpec = (spec == null) ? distinctSpec : spec.and(distinctSpec);

        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 20,
                sort
        );

        return repo.findAll(finalSpec, pageable);
    }


    @Override
    @Transactional
    public ProductVariant create(ProductVariantRequestDTO dto) {

        Product product = productService.getOrThrow(dto.getProductId());
        Size size = sizeService.getOrThrow(dto.getSizeId());

        ensureUniqueProductAndSizeOrThrow(product, size);

        ProductVariant productVariant = ProductVariant.builder()
                .product(product)
                .size(size)
                .priceInCents(dto.getPriceInCents())
                .stock(dto.getStock())
                .sku(generateSkuValue(product, size))
                .build();

        return repo.save(productVariant);
    }

    @Override
    @Transactional
    public ProductVariant update(Long id, ProductVariantUpdateDTO dto) {
        ProductVariant productVariant = getOrThrow(id);

        mapper.updateEntityFromDTO(dto, productVariant);

        return productVariant;
    }

    // -------- Convenience ops --------

    @Override
    @Transactional
    public ProductVariantResponseDTO setStock(Long variantId, Long stock) {
        var existing = ProductVariantService.super.getOrThrow(variantId);
        existing.setStock(stock != null ? Math.max(0L, stock) : 0L);
        return mapper.toResponseDTO(repo.save(existing));
    }

    @Override
    @Transactional
    public ProductVariantResponseDTO incrementStock(Long variantId, Long by) {
        var existing = ProductVariantService.super.getOrThrow(variantId);
        existing.setStock(Math.max(0L, (existing.getStock() == null ? 0L : existing.getStock()) + (by == null ? 0L : by)));
        return mapper.toResponseDTO(repo.save(existing));
    }

    @Override
    @Transactional
    public ProductVariantResponseDTO decrementStock(Long variantId, Long by) {
        var existing = getOrThrow(variantId);
        existing.setStock(Math.max(0L, (existing.getStock() == null ? 0L : existing.getStock()) - (by == null ? 0L : by)));
        return mapper.toResponseDTO(repo.save(existing));
    }

    // -------- Helpers --------
    private void ensureUniqueProductAndSizeOrThrow(Product product, Size size) {
        if (repo.existsByProductAndSize(product, size)) {
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
}
