package com.makibeans.search.utils;

import jakarta.persistence.criteria.*;

public final class SearchCriteriaUtils {
    private SearchCriteriaUtils() {}

    /** Resolve a dotted path with LEFT joins for each relation segment. */
    @SuppressWarnings({"unchecked","rawtypes"})
    public static <E> Path<?> resolvePath(Root<E> root, String dottedPath) {
        String[] parts = dottedPath.split("\\.");
        From<?,?> from = root;
        for (int i = 0; i < parts.length - 1; i++) {
            from = from.join(parts[i], JoinType.LEFT);
        }
        return parts.length == 0 ? root : from.get(parts[parts.length - 1]);
    }

    public static Expression<String> toLower(Path<?> path, CriteriaBuilder cb) {
        return cb.lower(path.as(String.class));
    }
}
