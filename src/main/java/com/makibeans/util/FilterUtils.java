package com.makibeans.util;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

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
     * Extract a String value (trims and filters blank).
     *
     * @param filters the filter map
     * @param key     the key to extract
     * @return an Optional containing the trimmed string, or empty if blank or not present
     */
    public static Optional<String> extractString(Map<String, String> filters, String key) {
        return extract(filters, key, Function.identity());
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
}
