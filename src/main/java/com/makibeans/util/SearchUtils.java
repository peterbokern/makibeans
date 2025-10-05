
package com.makibeans.util;

import com.makibeans.dto.search.SearchRequestDTO;

import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

public final class SearchUtils {

    private SearchUtils() {}

    /**
     * Builds a pageable object from SearchRequestDTO and sort mapping.
     */
    public static Pageable buildPageable(SearchRequestDTO req, Map<String, String> sortMapping) {
        String sortKey = Optional.ofNullable(req.getSortBy()).orElse("id").toLowerCase();
        String sortPath = sortMapping.getOrDefault(sortKey, "id");
        Sort.Direction dir = "desc".equalsIgnoreCase(req.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;

        int page = Optional.ofNullable(req.getPage()).filter(p -> p >= 0).orElse(0);
        int size = Optional.ofNullable(req.getSize()).filter(s -> s > 0).orElse(20);

        return PageRequest.of(page, size, Sort.by(dir, sortPath));
    }

    /**
     * Builds a dynamic Specification using search fields and filters.
     */
    // BEFORE: Map<String, Function<String, Specification<E>>>
    public static <E> Specification<E> buildSpecification(
            SearchRequestDTO req,
            List<String> textFields,
            Map<String, java.util.function.Function<Object, Specification<E>>> filterSpecs // 👈 raw Object now
    ) {
        Specification<E> spec = Specification.where(null);

        // free-text
        if (req.getSearch() != null && !req.getSearch().isBlank() && !textFields.isEmpty()) {
            String like = "%" + req.getSearch().trim().toLowerCase() + "%";
            spec = spec.and((root, q, cb) ->
                    textFields.stream()
                            .map(p -> cb.like(cb.lower(castToString(resolvePath(root, p))), like))
                            .reduce(cb::or)
                            .orElse(cb.conjunction()));
        }

        // filters
        Map<String, Object> f = req.getFilters() == null ? java.util.Map.of() : req.getFilters();
        for (var e : f.entrySet()) {
            var fn = (filterSpecs == null) ? null : filterSpecs.get(e.getKey());
            if (fn != null) {
                spec = spec.and(fn.apply(e.getValue())); // 👈 pass raw object (String | Number | Boolean | List | Array)
            }
        }
        return spec;
    }


    /** Resolves dot-separated path like "category.name" → root.join("category").get("name") */
    public static Path<?> resolvePath(Root<?> root, String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        Path<?> current = root;
        for (int i = 0; i < parts.length; i++) {
            boolean last = (i == parts.length - 1);
            String part = parts[i];
            if (!last && current instanceof From<?, ?> from) {
                current = from.join(part, JoinType.LEFT);
            } else {
                current = current.get(part);
            }
        }
        return current;
    }

    @SuppressWarnings("unchecked")
    public static Expression<String> castToString(Path<?> path) {
        return (Expression<String>) path;
    }

    /** Converts string to target type if possible */
    public static Object convert(String raw, Class<?> targetType) {
        if (raw == null) return null;
        try {
            if (targetType == Long.class || targetType == long.class) return Long.valueOf(raw);
            if (targetType == Integer.class || targetType == int.class) return Integer.valueOf(raw);
            if (targetType == Boolean.class || targetType == boolean.class) return Boolean.valueOf(raw);
            return raw;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid value '" + raw + "' for " + targetType.getSimpleName(), e);
        }
    }
}
