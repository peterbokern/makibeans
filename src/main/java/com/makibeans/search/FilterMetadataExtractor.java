package com.makibeans.search;

import com.makibeans.search.annotation.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Extracts filter/sort metadata from a filter DTO class.
 *
 * Important:
 * - Includes inherited fields (superclasses), so AdminFilter extends PublicFilter works.
 * - Maintains stable insertion order (LinkedHashMap).
 * - Child class fields override parent fields if they share the same resolved key.
 */
public class FilterMetadataExtractor {

    private static final Logger logger = LoggerFactory.getLogger(FilterMetadataExtractor.class);
    public static <F> Map<String, FilterMetadata> extract(Class<F> filterClass) {
        Map<String, FilterMetadata> out = new LinkedHashMap<>();

        // Walk class hierarchy from top -> bottom so child fields can override parent fields deterministically.
        List<Class<?>> hierarchy = new ArrayList<>();
        for (Class<?> c = filterClass; c != null && c != Object.class; c = c.getSuperclass()) {
            hierarchy.add(c);
        }
        Collections.reverse(hierarchy);

        logger.info("Extracting filter metadata for class hierarchy: {}", hierarchy.toString());

        for (Class<?> c : hierarchy) {
            for (Field f : c.getDeclaredFields()) {
                Filter ann = f.getAnnotation(Filter.class);
                if (ann == null || !ann.filterable()) continue;

                String key = ann.key().isBlank() ? f.getName() : ann.key();
                String path = ann.path().isBlank() ? f.getName() : ann.path();

                logger.info("Found filterable field: class={}, field={}, key={}, path={}, operations={}, sortable={}",
                        c.getSimpleName(), f.getName(), key, path, Arrays.toString(ann.type()), ann.sortable());

                // Child class with same key should override parent definition.
                out.put(key, new FilterMetadata(key, path, ann.type(), ann.sortable()));
            }
        }

        return out;
    }

    public record FilterMetadata(
            String key,
            String path,
            Filter.Operation[] operations,
            boolean sortable
    ) {}
}
