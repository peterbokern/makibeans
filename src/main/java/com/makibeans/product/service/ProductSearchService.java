package com.makibeans.product.service;

import com.makibeans.common.util.TextUtils;
import com.makibeans.product.filter.ProductFilterBase;
import com.makibeans.product.model.Product;
import com.makibeans.product.repository.AttributeValuePair;
import com.makibeans.product.repository.ProductRepository;
import com.makibeans.product.search.AttributeFilterSpecifications;
import com.makibeans.product.search.ProductVariantSpecifications;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import com.makibeans.web.exceptions.BadRequestException;
import com.makibeans.web.exceptions.InvalidAttributeFilterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductSearchService {

    private final ProductRepository repo;

    public ProductSearchService(
            ProductRepository repo) {
        this.repo = repo;
    }

    public <F extends ProductFilterBase> Page<Product> search(SearchRequest<F> req, Class<F> filterClass) {

        Specification<Product> spec = SpecificationFactory.fromRequest(req, filterClass);

        var filters = req.getFilters();

        //apply attribute value filters
        var rawAttributeFilters = filters.getAttributeFilters();
        var cleanAttributeFilters = validateAndNormalizeAttributeFilters(rawAttributeFilters);

        if (!cleanAttributeFilters.isEmpty()) {

            //if attributeFilters provided, 1 categoryId must be provided.
            var categoryIds = filters.getCategoryId();
            assertSingleCategoryId(categoryIds);

            //validate if requested attribute filters match with db
            var requestedPairs = buildRequestedPairs(cleanAttributeFilters);
            var attributeKeys = cleanAttributeFilters.keySet();
            var valueKeys = cleanAttributeFilters.entrySet().stream().flatMap(e -> e.getValue().stream()).collect(Collectors.toSet());
            var validPairs = fetchValidPairsFromDb(attributeKeys, valueKeys, categoryIds.get(0));

            assertRequestedPairsAreValid(requestedPairs, validPairs);

            spec = spec.and(applyAttributeFilters(cleanAttributeFilters));
        }

        //apply product variant specification.
        spec = spec.and(applyProductVariantFilters(filters));

        //pageable
        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 20,
                new SortResolver(filterClass)
                        .resolve(req.getSortBy(), req.getSortDirection()));

        return repo.findAll(spec, pageable);
    }

    private static void assertSingleCategoryId(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.size() != 1) {
            throw new BadRequestException("If the search request contains attributeFilters, exactly 1 categoryId must be provided.");
        }
    }

    private Specification<Product> applyAttributeFilters(Map<String, List<String>> filters) {
        Specification<Product> spec = Specification.where(null);

        for (var e : filters.entrySet()) {
            spec = spec.and(AttributeFilterSpecifications.hasAttributeValue(e.getKey(), e.getValue()));
        }
        return spec;
    }

    private Specification<Product> applyProductVariantFilters(ProductFilterBase filters){

        return ProductVariantSpecifications.hasMatchingVariant(
                filters.getMinPriceInCents(),
                filters.getMaxPriceInCents(),
                filters.getInStock(),
                filters.getSku(),
                filters.getSizeId());
    }

    private Map<String, List<String>> validateAndNormalizeAttributeFilters(Map<String, List<String>> raw) {
        // handle empty input
        if (raw == null || raw.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, List<String>> clean = new LinkedHashMap<>();

        // loop through entries, validate and normalize
        for (var entry : raw.entrySet()) {
            String attributeKey = entry.getKey();

            if (attributeKey == null || attributeKey.isBlank()) {
                throw new BadRequestException(
                        "Attribute filter keys must not be null or blank."
                );
            }

            String normalizedAttributeKey = normalize(attributeKey);

            if (normalizedAttributeKey == null || normalizedAttributeKey.isBlank()) {
                throw new BadRequestException(
                        "Attribute filter keys must not be null or blank."
                );
            }

            List<String> rawValues = entry.getValue();

            if (rawValues == null) {
                throw new BadRequestException(
                        "Attribute filter values must be provided as a list."
                );
            }

            Set<String> cleanValues = new LinkedHashSet<>();

            for (String v : rawValues) {
                if (v == null || v.isBlank()) {
                    throw new BadRequestException(
                            "Attribute filter '" + attributeKey + "' contains an invalid value."
                    );
                }

                String normalizedValue = normalize(v);

                if (normalizedValue == null || normalizedValue.isBlank()) {
                    throw new BadRequestException(
                            "Attribute filter '" + attributeKey + "' contains an invalid value."
                    );
                }

                cleanValues.add(normalizedValue);
            }

            // if empty list after cleaning -> no-op
            if (!cleanValues.isEmpty()) {
                clean.put(normalizedAttributeKey, new ArrayList<>(cleanValues));
            }
        }

        return clean;
    }

    private Set<String> buildRequestedPairs(Map<String, List<String>> clean) {
        if (clean == null || clean.isEmpty()) return Collections.emptySet();

        return clean.entrySet().stream()
                .flatMap(e -> e.getValue().stream().map(v -> pair(e.getKey(), v))).collect(Collectors.toSet());
    }

    //fetch valid attribute value pairs from db for a category including only fields where categoryAttribute.filterable = true
    private Set<String> fetchValidPairsFromDb(Set<String> attributeKeys, Set<String> valueKeys, Long categoryId) {

        List<AttributeValuePair> pairs = repo.findValidAttributeValuePairsForCategory(categoryId, attributeKeys, valueKeys);

        if (pairs == null || pairs.isEmpty()) return Collections.emptySet();

        return pairs.stream()
                .filter(p -> p.getAttributeKey() != null && !p.getAttributeKey().isBlank())
                .filter(p -> p.getValueKey() != null && !p.getValueKey().isBlank())
                .map(p -> pair(normalize(p.getAttributeKey()), normalize(p.getValueKey())))
                .collect(Collectors.toSet());
    }

    private void assertRequestedPairsAreValid(Set<String> requested, Set<String> existing) {

        if (requested == null || requested.isEmpty()) return;

        Set<String> invalidPairs = new HashSet<>(requested);
        invalidPairs.removeAll(existing);

        if (!invalidPairs.isEmpty()) {
            throw new InvalidAttributeFilterException(invalidPairs);
        }
    }

    private String pair(String k, String v) {
        return k + "=" + v;
    }

    private String normalize(String in) {
        return (in == null) ? null : TextUtils.normalizeText(in);
    }
}
