package com.makibeans.attribute.attribute.model;

import lombok.Getter;

@Getter
public enum AttributeInputType {

    // Free input (stored in raw_value)
    FREE_TEXT("text", false),
    NUMERIC("numeric", false),
    DATE_PICKER("date", false),
    SLIDER("slider", false),
    CHECKBOX("checkbox", false),

    // Library-backed (stored via AttributeValue)
    DROPDOWN("dropdown", true),
    MULTISELECT("multiselect", true);

    private final String label;
    private final boolean libraryBacked;

    AttributeInputType(String label, boolean libraryBacked) {
        this.label = label;
        this.libraryBacked = libraryBacked;
    }
}
