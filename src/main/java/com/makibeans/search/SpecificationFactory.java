package com.makibeans.search;

import com.makibeans.search.annotation.Filter;
import com.makibeans.search.utils.SearchCriteriaUtils;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.*;

public final class SpecificationFactory {

    private static final Logger logger = LoggerFactory.getLogger(SpecificationFactory.class);

    private SpecificationFactory() {
    }

    /**
     * Single entrypoint: build full spec (filters AND free-text search + includeDeleted handling).
     */
    public static <E, F> Specification<E> fromRequest(SearchRequest<F> req, Class<F> filterClass) {
        if (req == null) return alwaysTrue();

        Specification<E> byFilters = fromFilters(req.getFilters(), filterClass);
        Specification<E> bySearch = freeTextSearch(req.getSearch(), filterClass);

        boolean includeDeleted = Boolean.TRUE.equals(req.getIncludeDeleted());
        boolean hasExplicitDeletedFilter = hasExplicitDeletedFilter(req.getFilters());

        Specification<E> spec = and(byFilters, bySearch);

        /**
         * If includeDeleted is true → do not add any automatic "not deleted" filter (return spec as-is).
         * If includeDeleted is false (or null/falsey) and the filters DTO contains an explicit deleted filter → respect that explicit filter.
         * If includeDeleted is false and the filters DTO does NOT have an explicit deleted filter → add notDeleted() to enforce deleted = false.
         * Notes:
         * hasExplicitDeletedFilter checks the filters DTO for a declared deleted field with a boolean value (inheritance-safe).
         * notDeleted() itself is safe: if the path deleted cannot be resolved at runtime it returns a conjunction() (no-op).
         */
        if (!includeDeleted && !hasExplicitDeletedFilter) {
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

        //loops through all field and checks if
        for (Field f : getAllFields(filterClass)) {
            logger.info("Inspecting field: {}", f.getName());

            Filter ann = f.getAnnotation(Filter.class);
            if (ann == null || !ann.filterable()) continue;

            f.setAccessible(true);
            Object value;
            try {
                value = f.get(filters); // example slug = "electronics"
            } catch (IllegalAccessException e) {
                continue;
            }
            if (value == null) continue;

            String path = ann.path().isBlank() ? f.getName() : ann.path();

            // Apply ONLY ONE operation per field (based on value type + allowed ops)
            Filter.Operation op = pickOperation(ann, value);
            if (op == null) continue;

            specs.add(build(path, op, value));
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

        for (Field f : getAllFields(filterClass)) {
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

    // -------------------------------------------------------------------------
    // FIELD INTROSPECTION HELPERS (INHERITANCE-SAFE)
    // -------------------------------------------------------------------------

    private static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    private static Field findField(Class<?> type, String name) {
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // OPERATION SELECTION
    // -------------------------------------------------------------------------

    private static Filter.Operation pickOperation(Filter ann, Object value) {
        Set<Filter.Operation> allowed = EnumSet.noneOf(Filter.Operation.class);
        allowed.addAll(Arrays.asList(ann.type()));

        if (value instanceof String) {
            if (allowed.contains(Filter.Operation.LIKE)) return Filter.Operation.LIKE;
            if (allowed.contains(Filter.Operation.EQ)) return Filter.Operation.EQ;
            return null;
        }

        if (value instanceof Boolean) {
            return allowed.contains(Filter.Operation.BOOL) ? Filter.Operation.BOOL : null;
        }

        if (value instanceof Collection<?> || value.getClass().isArray()) {
            return allowed.contains(Filter.Operation.IN) ? Filter.Operation.IN : null;
        }

        // Range values should be separate fields (From/To) that declare only GTE/LTE
        if (allowed.contains(Filter.Operation.GTE)) return Filter.Operation.GTE;
        if (allowed.contains(Filter.Operation.LTE)) return Filter.Operation.LTE;

        return allowed.contains(Filter.Operation.EQ) ? Filter.Operation.EQ : null;
    }

    // -------------------------------------------------------------------------
    // INCLUDE DELETED HANDLING
    // -------------------------------------------------------------------------

    // Check if filters DTO has an explicit 'deleted' filter set (inheritance-safe)
    private static <F> boolean hasExplicitDeletedFilter(F filters) {
        if (filters == null) return false;

        try {
            Field deletedField = findField(filters.getClass(), "deleted");
            if (deletedField == null) return false;

            deletedField.setAccessible(true);
            Object value = deletedField.get(filters);
            return value instanceof Boolean;
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    // NOTE: kept for compatibility if referenced elsewhere; not used in this factory currently.
    @SuppressWarnings("unused")
    private static <E> boolean hasDeletedField(Class<E> entityClass) {
        try {
            entityClass.getDeclaredField("deleted");
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
}
