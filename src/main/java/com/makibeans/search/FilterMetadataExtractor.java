package com.makibeans.search;

import com.makibeans.search.annotation.Filter;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class FilterMetadataExtractor {

    public static Map<String, FilterMetadata> extract(Class<?> filterClass) {
        Map<String, FilterMetadata> out = new LinkedHashMap<>();
        for (Field f : filterClass.getDeclaredFields()) {
            Filter ann = f.getAnnotation(Filter.class); //e.g @Filter(key="categoryId", path="category.id", type={EQ, IN}, filterable=true, sortable=true)
            if (ann == null || !ann.filterable()) continue;

            String key = ann.key().isBlank() ? f.getName() : ann.key(); //e.g. "categoryId"
            String path = ann.path().isBlank() ? f.getName() : ann.path(); //e.g. "category.id"

            out.put(key, new FilterMetadata(key, path, ann.type(), ann.sortable()));
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
