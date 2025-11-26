package com.makibeans.attribute.attribute.model;

import lombok.Getter;
/**
 * Enum representing different data types for attributes.
 */
@Getter
public enum AttributeDataType {
    STRING("String"),
    NUMERIC("Number"),
    BOOLEAN("Boolean"),
    DATE("Date"),
    DATETIME("DateTime");

    private final String label;

    AttributeDataType(String label) {
        this.label = label;
    }
}
