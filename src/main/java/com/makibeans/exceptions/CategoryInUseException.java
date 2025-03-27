package com.makibeans.exceptions;

public class CategoryInUseException extends RuntimeException {
    public CategoryInUseException(String message) {
        super(message);
    }
}

