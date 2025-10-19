package com.makibeans.search;

import org.springframework.data.domain.Sort;

import java.util.Map;

/**
 * Resolves sortBy -> entity path using @Filter metadata.
 * If not present in metadata, allows a single safe fallback: "id" (root-level).
 */
public final class SortResolver {
    private final Map<String, FilterMetadataExtractor.FilterMetadata> meta;

    public SortResolver(Class<?> filterClass) {
        this.meta = FilterMetadataExtractor.extract(filterClass);
    }

    public Sort resolve(String sortBy, SortDirection dir) {
        if (sortBy == null || sortBy.isBlank()) return Sort.unsorted();

        // 1) If annotated as sortable -> use its declared path (join-capable)
        var m = meta.get(sortBy);
        if (m != null && m.sortable()) {
            return Sort.by(toSpringDir(dir), m.path());
        }

        // 2) Single built-in fallback: allow root-level "id"
        if ("id".equals(sortBy)) {
            return Sort.by(toSpringDir(dir), "id");
        }

        // 3) Unknown or not allowed -> unsorted
        return Sort.unsorted();
    }

    private static Sort.Direction toSpringDir(SortDirection dir) {
        return (dir == SortDirection.DESC)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
    }
}
