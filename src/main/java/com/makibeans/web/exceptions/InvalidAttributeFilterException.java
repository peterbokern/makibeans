package com.makibeans.web.exceptions;

import lombok.Getter;

import java.util.Set;

public class InvalidAttributeFilterException extends BadRequestException {

    private final Set<String> invalidPairs;

    public InvalidAttributeFilterException(Set<String> invalidPairs) {
        super("One or more attribute filters are not valid for the selected category.");
        this.invalidPairs = invalidPairs;
    }

    public Set<String> getInvalidPairs() {
        return invalidPairs;
    }
}
