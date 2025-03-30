package com.makibeans.util;

import org.mapstruct.Named;

/**
 * Utility class for mapping operations.
 */

public class MappingUtils {

    @Named("normalizeValue")
    public static String normalizeValue(String value) {
        return value.trim().toLowerCase();
    }
}