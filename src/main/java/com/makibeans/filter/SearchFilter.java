package com.makibeans.filter;

import com.makibeans.util.FilterUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class SearchFilter {

    /**
     * Filters a list of objects using search parameters and a list of getter functions.
     *
     * @param items        the list of objects to filter
     * @param searchParams the map of search parameters
     * @param getters      map of field getters (e.g. MyObject::getName, MyObject::getDescription)
     * @param <T>          the type of object
     * @return a filtered list containing only objects where at least one field contains the query
     */

    public static <T> List<T> apply(List<T> items, Map<String,String> searchParams, Map<String, Function<T, String>> getters) {

        // Extract the searchParams
        String query = FilterUtils.extractLowerCase(searchParams, "search").orElse(null);
        String sort = FilterUtils.extractLowerCase(searchParams, "sort").orElse(null);
        String order = FilterUtils.extractLowerCase(searchParams, "order").orElse("desc");

        if (query == null || query.isBlank()) return items;

        Stream<T> stream = items.stream();

        stream = stream
                .filter(item ->
                        getters.values().stream().anyMatch(getter ->
                                Optional.ofNullable(getter.apply(item)) // e.g attributeTemplate.getName()
                                        .map(String::toLowerCase)
                                        .map(field -> field.contains(query))
                                        .orElse(false)
                        ));

        //sort
        if (sort != null && getters.containsKey(sort)) {

            Comparator<T> comparator = Comparator.comparing(item ->
                    Optional.ofNullable(getters.get(sort)
                    .apply(item))
                    .orElse(""));

            if ("desc".equals(order)) {
                comparator = comparator.reversed();
            }

            stream = stream.sorted(comparator);
        }

        return stream.toList();
    }
}
