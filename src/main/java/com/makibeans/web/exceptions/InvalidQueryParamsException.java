package com.makibeans.web.exceptions;
/**
 * Exception thrown when query parameters are invalid.
 */


import lombok.Getter;

import java.util.List;

@Getter
public class InvalidQueryParamsException extends RuntimeException {
    private final List<String> unknownParams;

    public InvalidQueryParamsException(List<String> unknownParams) {
        super("Unknown query parameter" + (unknownParams.size() == 1 ? "" : "s")
                + ": " + String.join(", ", unknownParams));
        this.unknownParams = unknownParams;
    }

}
