package com.makibeans.web.exceptions;


import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter @Getter
public class UnknownQueryParamException extends RuntimeException {
    private final String paramName;
    public UnknownQueryParamException(String paramName, Set<String> allowedParams) {
        super("Unknown query parameter: " + paramName);
        this.paramName = paramName;
    }

}
