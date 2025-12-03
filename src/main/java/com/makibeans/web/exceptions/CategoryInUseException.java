package com.makibeans.web.exceptions;

/**
 * Exception thrown when a category is in use and cannot be deleted.
 */

public class CategoryInUseException extends RuntimeException {
    public CategoryInUseException(String message) {
        super(message);
    }
}

