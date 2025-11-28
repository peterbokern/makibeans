package com.makibeans.attribute.attribute.model;

import lombok.Getter;
/**
 * Enum representing different data types for attributes.
 */
@Getter
public enum AttributeDataType {
    STRING("string"),
    NUMERIC("number"),
    BOOLEAN("boolean"),
    DATE("date"),
    DATETIME("dateTime");

    private final String label;

    AttributeDataType(String label) {
        this.label = label;
    }
}
