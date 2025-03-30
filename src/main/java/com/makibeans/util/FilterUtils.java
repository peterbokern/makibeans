package com.makibeans.util;

import com.makibeans.exceptions.InvalidFilterException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FilterUtils {

    /**
     * Generic extraction method for parsing filter values.
     *
     * @param filters   the map of filters
     * @param key       the key to extract
     * @param converter a function to convert the string value to the desired type
     * @param <T>       the target type
     * @return an Optional containing the parsed value, or empty if not present or blank
     */

    public static <T> Optional<T> extract(Map<String, String> filters, String key, Function<String, T> converter) {
        return Optional.ofNullable(filters.get(key))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(converter);
    }

    /**
     * Extract a String value in lowercase (trims and filters blank).
     *
     * @param filters the filter map
     * @param key     the key to extract
     * @return an Optional containing the lowercase trimmed string, or empty if blank or not present
     */

    public static Optional<String> extractLowerCase(Map<String, String> filters, String key) {
        return extract(filters, key, String::toLowerCase);
    }

    /**
     * Extract a Long value.
     *
     * @param filters the filter map
     * @param key     the key to extract
     * @return an Optional containing the parsed Long, or empty if blank or not a valid number
     */

    public static Optional<Long> extractLong(Map<String, String> filters, String key) {
        try {
            return extract(filters, key, Long::parseLong);
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    /**
     * Extract an Integer value.
     *
     * @param filters the filter map
     * @param key     the key to extract
     * @return an Optional containing the parsed Integer, or empty if blank or not a valid number
     */

    public static Optional<Integer> extractInt(Map<String, String> filters, String key) {
        try {
            return extract(filters, key, Integer::parseInt);
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    /**
     * Splits a comma-separated string into a cleaned list of lowercase, trimmed strings.
     *
     * @param raw The raw comma-separated string
     * @return A list of cleaned strings
     */

    public static List<String> splitAndNormalize(String raw) {
        return Optional.ofNullable(raw)
                .map(s -> s.split(","))
                .stream()
                .flatMap(Arrays::stream) //convert Stream<String[]> into Stream<String>
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isBlank())
                .toList();
    }

    /**
     * Extracts a comma-separated string from the filter map, splits it into a list of cleaned, lowercase, trimmed strings.
     *
     * @param filters the filter map
     * @param key     the key to extract
     * @return a list of cleaned, lowercase, trimmed strings, or an empty list if the key is not present or the value is blank
     */

    public static List<String> extractStringList(Map<String, String> filters, String key) {
        return Optional.ofNullable(filters.get(key))
                .map(FilterUtils::splitAndNormalize)
                .orElse(List.of());
    }

    /**
     * Extracts a comma-separated string from the filter map, splits it into a list of cleaned, trimmed Long values.
     *
     * @param filters the filter map
     * @param key     the key to extract
     * @return a list of cleaned, trimmed Long values, or an empty list if the key is not present or the value is blank
     * @throws NumberFormatException if any of the values cannot be parsed as Long
     */

    public static List<Long> extractLongList(Map<String, String> filters, String key) {
        return Optional.ofNullable(filters.get(key))
                .map(value -> Arrays.stream(value.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .map(Long::parseLong)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    /**
     * Validates the search parameters against a set of allowed parameters.
     * Throws an InvalidFilterException if any parameter is not allowed.
     *
     * @param searchParams  the map of search parameters to validate
     * @param allowedParams the set of allowed parameter names
     * @throws InvalidFilterException if any parameter in searchParams is not in allowedParams
     */

    public static void validateParams(Map<String, String> searchParams, Set<String> allowedParams) {

        List<String> invalid = searchParams.keySet()
                .stream()
                .filter(p -> !allowedParams.contains(p))
                .toList();

        if (!invalid.isEmpty()) throw new InvalidFilterException("Invalid filter parameter(s): " + invalid);
    }
}
