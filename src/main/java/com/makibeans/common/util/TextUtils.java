package com.makibeans.common.util;

import java.util.List;

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

    public static  String toCommaDelimitedString(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        if (items.size() == 1) {
            return items.getFirst() + ".";
        }
        if (items.size() == 2) {
            return items.getFirst()+ " and " + items.getLast() + ".";
        }
        String joined = String.join(", ", items.subList(0, items.size() - 1));
        joined += ", and " + items.getLast();
        return joined + ".";
    }
}
