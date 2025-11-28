package com.makibeans.common.util;

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

    public static String toSlug(String input) {
        if (input == null) return null;

        String slug = input
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")   // remove special chars
                .replaceAll("\\s+", "-")          // spaces → dash
                .replaceAll("-+", "-");           // collapse multiple dashes

        return slug.isEmpty() ? null : slug;
    }
}
