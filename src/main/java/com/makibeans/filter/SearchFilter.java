package com.makibeans.filter;

import com.makibeans.util.FilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class SearchFilter {

    private static final Logger logger = LoggerFactory.getLogger(SearchFilter.class);

/**
 * Filters a list of objects using search parameters and a list of getter functions.
 *
 * @param items            the list of objects to filter
 * @param searchParams     the map of search parameters
 * @param searchFields     map of field getters (e.g. MyObject::getName, MyObject::getDescription)
 * @param sortFields       map of field comparators for sorting
 * @param <T>              the type of object
 * @return a filtered list containing only objects where at least one field contains the query
 */

    public static <T> List<T> apply(
            List<T> items,
            Map<String, String> searchParams,
            Map<String, Function<T, String>> searchFields,
            Map<String, Comparator<T>> sortFields){

        logger.info("Applying filter and sort on (class =  {} ) with search parameters: {}", items.isEmpty() ? "Unknown" : items.get(0).getClass().getSimpleName(), searchParams);

        //extract the search parameters
        String query = FilterUtils.extractLowerCase(searchParams, "search").orElse(null);
        String sort = FilterUtils.extractLowerCase(searchParams, "sort").orElse("id");
        String order = FilterUtils.extractLowerCase(searchParams, "order").orElse("asc");


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

        // Sort and default to id field if present and no sort field is provided
        Comparator<T> comparator = sortFields.getOrDefault(sort, null);

        if (comparator != null) {
            stream = "desc".equals(order) ? stream.sorted(comparator.reversed()) : stream.sorted(comparator);
        }

        return stream.toList();
    }
}
