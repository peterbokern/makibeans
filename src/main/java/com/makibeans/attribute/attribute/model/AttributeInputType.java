package com.makibeans.attribute.attribute.model;

import lombok.Getter;

@Getter
public enum AttributeInputType {
    TEXT("text"),        // Free text input
    DROPDOWN("dropdown"),    // Select one from predefined values
    MULTISELECT("multiselect"), // Select multiple values
    NUMERIC("numeric"),      // Numeric input
    SLIDER("slider"),      // Slider UI
    DATE_PICKER("datepicker"), // Date picker
    CHECKBOX("checkbox");   // Boolean checkbox ;

    private final String label;

    AttributeInputType(String label) {
        this.label = label;
    }
}