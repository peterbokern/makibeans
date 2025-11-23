package com.makibeans.util;

public class TextUtils {

    private TextUtils() {
        // Utility class
    }

    public static String normalizeText(String input) {
        if (input == null) return null;

        String normalized = input.toLowerCase().replaceAll("\\s+", " ").trim();

        return normalized.isEmpty() ? null : normalized;
    }

    public static String trim(String input) {
        if (input == null) return null;
        String trimmed = input.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
