package com.makibeans.web.exceptions;


import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class UnknownQueryParamException extends RuntimeException {
    private final String paramName;
    public UnknownQueryParamException(String paramName) {
        super("Unknown query parameter: " + paramName);
        this.paramName = paramName;
    }

}
