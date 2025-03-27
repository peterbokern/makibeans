package com.makibeans.util;

/**
 * Utility class for handling updates and normalization of strings.
 */
public class UpdateUtils {

    /**
     * Determines if an update is needed by comparing the new value with the current value.
     *
     * @param newValue the new value to check
     * @param currentValue the current value to compare against
     * @return true if the new value is not null, not blank, and different from the current value after normalization; false otherwise
     */
    public static boolean shouldUpdate(String newValue, String currentValue) {
        return newValue != null && !newValue.isBlank() &&
                !normalize(newValue).equalsIgnoreCase(normalize(currentValue));
    }

    /**
     * Normalizes the given string by trimming whitespace and converting to lowercase.
     *
     * @param value the string to normalize
     * @return the normalized string, or an empty string if the input is null
     */

    public static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

}
