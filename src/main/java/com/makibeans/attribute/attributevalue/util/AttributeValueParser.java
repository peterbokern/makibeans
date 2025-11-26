package com.makibeans.attribute.attributevalue.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public final class AttributeValueParser {

    private AttributeValueParser() {
        // utility class
    }

    public static BigDecimal parseNumeric(String raw) throws IllegalArgumentException {
        try {
            return new BigDecimal(raw);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid numeric value: '" + raw + "'");
        }
    }

    public static boolean parseBoolean(String raw) throws IllegalArgumentException {
        if (raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(raw);
        }
        throw new IllegalArgumentException("Invalid boolean value: '" + raw + "'. Allowed: true or false.");
    }

    public static LocalDate parseDate(String raw) throws IllegalArgumentException {
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(
                    "Invalid date value: '" + raw + "'. Expected format: yyyy-MM-dd"
            );
        }
    }

    public static LocalDateTime parseDateTime(String raw) throws IllegalArgumentException {
        try {
            return LocalDateTime.parse(raw);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(
                    "Invalid date-time value: '" + raw + "'. Expected format: yyyy-MM-dd'T'HH:mm:ss"
            );
        }
    }
}
