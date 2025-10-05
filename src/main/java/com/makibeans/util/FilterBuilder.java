package com.makibeans.util;

import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.function.Function;

public final class FilterBuilder<E> {

    private final java.util.Map<String, java.util.function.Function<Object, Specification<E>>> map = new java.util.HashMap<>();

    public java.util.Map<String, java.util.function.Function<Object, Specification<E>>> build() {
        return java.util.Map.copyOf(map);
    }

    /** Exact match. If value is a collection/array -> converts to IN(). */
    public FilterBuilder<E> eq(String key, String fieldPath) {
        map.put(key, raw -> (root, q, cb) -> {
            var path = SearchUtils.resolvePath(root, fieldPath);
            var cls  = path.getJavaType();

            var values = toObjects(raw);
            if (values.size() > 1) {
                var converted = values.stream().map(v -> SearchUtils.convert(v.toString(), cls)).toList();
                return path.in(converted);
            } else {
                Object v = values.isEmpty() ? null : values.get(0);
                Object converted = v == null ? null : SearchUtils.convert(v.toString(), cls);
                return cb.equal(path, converted);
            }
        });
        return this;
    }

    /** Boolean eq (accepts boolean, "true"/"false"). */
    public FilterBuilder<E> bool(String key, String fieldPath) {
        map.put(key, raw -> (root, q, cb) -> {
            boolean val = toBoolean(raw);
            return cb.equal(SearchUtils.resolvePath(root, fieldPath), val);
        });
        return this;
    }

    /** Case-insensitive LIKE. If list/array given, OR the likes. */
    public FilterBuilder<E> like(String key, String fieldPath) {
        map.put(key, raw -> (root, q, cb) -> {
            var path = SearchUtils.resolvePath(root, fieldPath);
            var terms = toStrings(raw);
            if (terms.isEmpty()) return cb.conjunction();
            return terms.stream()
                    .map(s -> "%" + s.toLowerCase() + "%")
                    .map(p -> cb.like(cb.lower(SearchUtils.castToString(path)), p))
                    .reduce(cb::or)
                    .orElse(cb.conjunction());
        });
        return this;
    }

    /** IN predicate. Accepts array/list/CSV/string. */
    public FilterBuilder<E> in(String key, String fieldPath) {
        map.put(key, raw -> (root, q, cb) -> {
            var path = SearchUtils.resolvePath(root, fieldPath);
            var cls  = path.getJavaType();
            var vals = toObjects(raw).stream()
                    .map(v -> v == null ? null : SearchUtils.convert(v.toString(), cls))
                    .toList();
            return vals.isEmpty() ? cb.conjunction() : path.in(vals);
        });
        return this;
    }

    /* ---------- helpers to normalize incoming raw values ---------- */

    private static java.util.List<Object> toObjects(Object raw) {
        if (raw == null) return java.util.List.of();
        if (raw instanceof java.util.Collection<?> c) return new java.util.ArrayList<>(c);
        if (raw.getClass().isArray()) {
            int len = java.lang.reflect.Array.getLength(raw);
            java.util.List<Object> list = new java.util.ArrayList<>(len);
            for (int i = 0; i < len; i++) list.add(java.lang.reflect.Array.get(raw, i));
            return list;
        }
        String s = raw.toString().trim();
        if (s.contains(",")) {
            return java.util.Arrays.stream(s.split(","))
                    .map(String::trim).filter(t -> !t.isEmpty())
                    .collect(java.util.stream.Collectors.toList());
        }
        return java.util.List.of(raw);
    }

    private static java.util.List<String> toStrings(Object raw) {
        return toObjects(raw).stream().map(o -> o == null ? "" : o.toString().trim()).filter(s -> !s.isEmpty()).toList();
    }

    private static boolean toBoolean(Object raw) {
        if (raw instanceof Boolean b) return b;
        return Boolean.parseBoolean(String.valueOf(raw));
    }
}
