package com.makibeans.filter;

import com.makibeans.util.FilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class SearchFilter {

    private static final Logger logger = LoggerFactory.getLogger(SearchFilter.class);
    private static final Set<String> SPECIAL_PARAMS = Set.of("sort", "order", "search", "page", "size");


    /**
     * Filters a list of objects using search parameters and a list of getter functions.
     *
     * @param items        the list of objects to filter
     * @param searchParams the map of search parameters
     * @param searchFields map of field getters (e.g. MyObject::getName, MyObject::getDescription)
     * @param sortFields   map of field comparators for sorting
     * @param <T>          the type of object
     * @return a filtered list containing only objects where at least one field contains the query
     */

    public static <T> List<T> apply(
            List<T> items,
            Map<String, String> searchParams,
            Map<String, Function<T, String>> searchFields,
            Map<String, Comparator<T>> sortFields) {


        logger.info("Applying filter and sort on (class =  {} ) with search parameters: {}", items.isEmpty() ? "Unknown" : items.get(0).getClass().getSimpleName(), searchParams);

        //extract the search parameters
        String query = FilterUtils.extractLowerCase(searchParams, "search").orElse(null);
        String sort = FilterUtils.extractLowerCase(searchParams, "sort").orElse("id");
        String order = FilterUtils.extractLowerCase(searchParams, "order").orElse("asc");

        //define allowed params
        Set<String> allowedParams = new HashSet<>(SPECIAL_PARAMS);
        allowedParams.addAll(searchFields.keySet());

        //validate query
        FilterUtils.validateParams(searchParams, allowedParams);

        //stream items
        Stream<T> stream = items.stream();

        // Apply search filter if present
        if (query != null && !query.isBlank()) {
            stream = stream.filter(item ->
                    searchFields.values().stream().anyMatch(getter ->
                            Optional.ofNullable(getter.apply(item)) // e.g attributeTemplate.getName()
                                    .map(String::toLowerCase)
                                    .map(val -> val.contains(query))
                                    .orElse(false)
                    )
            );
        }

        //apply filtering on search fields
        for (Map.Entry<String, String> entry : searchParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            //skip special params (sort, order, etc) and fields that don't have a getter.
            if (SPECIAL_PARAMS.contains(key) || !searchFields.containsKey(key)) continue;

            Function<T, String> getter = searchFields.get(key);

            stream = stream.filter(item ->
                    Optional.ofNullable(getter.apply(item))
                            .map(String::toLowerCase)
                            .map(String::trim)
                            .map(val -> val.equals(value.toLowerCase().trim()))
                            .orElse(false));
        }

        // Sort and default to id field if present and no sort field is provided
        Comparator<T> comparator = sortFields.getOrDefault(sort, null);

        if (comparator != null) {
            stream = "desc".equals(order)
                    ? stream.sorted(comparator.reversed())
                    : stream.sorted(comparator);
        }

        return stream.toList();
    }
}
