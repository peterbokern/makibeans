package com.makibeans.search;

import com.makibeans.search.annotation.Filter;
import com.makibeans.search.utils.SearchCriteriaUtils;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.*;

public final class SpecificationFactory {
    private SpecificationFactory() {
    }

    /**
     * Single entrypoint: build full spec (filters AND free-text search + includeDeleted handling).
     */
    public static <E, F> Specification<E> fromRequest(SearchRequest<F> req, Class<F> filterClass) {
        if (req == null) {
            return alwaysTrue();
        }

        Specification<E> byFilters = fromFilters(req.getFilters(), filterClass);
        Specification<E> bySearch = freeTextSearch(req.getSearch(), filterClass);

        boolean includeDeleted = Boolean.TRUE.equals(req.getIncludeDeleted());
        boolean hasExplicitDeletedFilter = hasExplicitDeletedFilter(req.getFilters());
        boolean hasDeletedField = hasDeletedField(filterClass);

        Specification<E> spec = and(byFilters, bySearch);


        /**
         * If includeDeleted is true → do not add any automatic "not deleted" filter (return spec as-is).
         * If includeDeleted is false (or null/falsey) and the entity class does NOT have a deleted field → nothing is added (can't enforce deleted=false).
         * If includeDeleted is false and the filters DTO contains an explicit deleted filter → respect that explicit filter and do NOT add the implicit notDeleted() filter.
         * If includeDeleted is false, the entity has a deleted field, and the filters DTO does NOT have an explicit deleted field → add notDeleted() to enforce deleted = false (i.e., hide deleted rows).
         * Notes:
         * hasExplicitDeletedFilter checks the filters DTO for a declared deleted field with a boolean value.
         * notDeleted() itself is safe: if the path deleted cannot be resolved at runtime it returns a conjunction() (no-op).
         */


        if (!includeDeleted && hasDeletedField && !hasExplicitDeletedFilter) {
            spec = and(spec, notDeleted());
        }
        return spec;
    }

    /**
     * Build spec from typed filter DTO annotated with @Filter.
     */
    public static <E, F> Specification<E> fromFilters(F filters, Class<F> filterClass) {
        if (filters == null) return alwaysTrue();

        List<Specification<E>> specs = new ArrayList<>();

        for (Field f : filterClass.getDeclaredFields()) {
            Filter ann = f.getAnnotation(Filter.class);
            if (ann == null || !ann.filterable()) continue;

            f.setAccessible(true);
            Object value;
            try {
                value = f.get(filters);
            } catch (IllegalAccessException e) {
                continue;
            }
            if (value == null) continue;

            String path = ann.path().isBlank() ? f.getName() : ann.path();
            for (Filter.Operation op : ann.type()) {
                Specification<E> s = build(path, op, value);
                specs.add(s);
            }
        }

        return specs.stream().reduce(alwaysTrue(), Specification::and);
    }

    /**
     * Free-text search: OR over all fields annotated with LIKE (String-typed).
     */
    public static <E> Specification<E> freeTextSearch(String search, Class<?> filterClass) {
        if (search == null || search.isBlank()) return alwaysTrue();

        final String needle = "%" + search.toLowerCase() + "%";
        List<Specification<E>> likeSpecs = new ArrayList<>();

        for (Field f : filterClass.getDeclaredFields()) {
            Filter ann = f.getAnnotation(Filter.class);
            if (ann == null || !ann.filterable()) continue;

            boolean allowsLike = Arrays.stream(ann.type()).anyMatch(op -> op == Filter.Operation.LIKE);
            if (!allowsLike) continue;

            String path = ann.path().isBlank() ? f.getName() : ann.path();
            likeSpecs.add((root, q, cb) ->
                    cb.like(SearchCriteriaUtils.toLower(SearchCriteriaUtils.resolvePath(root, path), cb), needle)
            );
        }

        if (likeSpecs.isEmpty()) return alwaysTrue();

        Specification<E> orSpec = likeSpecs.get(0);
        for (int i = 1; i < likeSpecs.size(); i++) {
            orSpec = orSpec.or(likeSpecs.get(i));
        }
        return orSpec;
    }

    /**
     * Safe AND helper (null-aware).
     */
    public static <E> Specification<E> and(Specification<E> a, Specification<E> b) {
        if (a == null) return b == null ? alwaysTrue() : b;
        if (b == null) return a;
        return a.and(b);
    }

    private static <E> Specification<E> alwaysTrue() {
        return (root, q, cb) -> cb.conjunction();
    }

    /**
     * Convenience specification that enforces deleted = false on entities that
     * have a boolean 'deleted' attribute. If the path cannot be resolved, this
     * effectively behaves as a no-op (conjunction).
     */
    private static <E> Specification<E> notDeleted() {
        return (root, q, cb) -> {
            try {
                Path<Boolean> deletedPath = root.get("deleted");
                return cb.isFalse(deletedPath);
            } catch (IllegalArgumentException ex) {
                // Entity does not have a 'deleted' property; ignore this filter.
                return cb.conjunction();
            }
        };
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <E> Specification<E> build(String path, Filter.Operation op, Object value) {
        return (root, q, cb) -> {
            Path<?> expr = SearchCriteriaUtils.resolvePath(root, path);
            return switch (op) {
                case EQ -> cb.equal(expr, value);
                case IN -> {
                    Collection<?> c = toCollection(value);
                    yield c.isEmpty() ? cb.conjunction() : expr.in(c);
                }
                case LIKE -> {
                    if (!(value instanceof String s)) yield cb.conjunction();
                    yield cb.like(SearchCriteriaUtils.toLower(expr, cb), "%" + s.toLowerCase() + "%");
                }
                case BOOL -> {
                    if (!(value instanceof Boolean b)) yield cb.conjunction();
                    yield b ? cb.isTrue(expr.as(Boolean.class)) : cb.isFalse(expr.as(Boolean.class));
                }
                case GTE -> {
                    if (!(value instanceof Comparable comp)) yield cb.conjunction();
                    yield cb.greaterThanOrEqualTo((Expression) expr, comp);
                }
                case LTE -> {
                    if (!(value instanceof Comparable comp)) yield cb.conjunction();
                    yield cb.lessThanOrEqualTo((Expression) expr, comp);
                }
            };
        };
    }

    private static Collection<?> toCollection(Object v) {
        if (v == null) return List.of();
        if (v instanceof Collection<?> c) return c;
        if (v.getClass().isArray()) return Arrays.asList((Object[]) v);
        return List.of(v);
    }

    // Check if filters DTO has an explicit 'deleted' filter set
    private static <F> boolean hasExplicitDeletedFilter(F filters) {
        if (filters == null) return false;

        try {
            Field deletedField = filters.getClass().getDeclaredField("deleted");
            deletedField.setAccessible(true);
            Object value = deletedField.get(filters);
            return value instanceof Boolean;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }

    //check if entity has deleted field
    private static <E> boolean hasDeletedField(Class<E> entityClass) {
        try {
            entityClass.getDeclaredField("deleted");
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
}
