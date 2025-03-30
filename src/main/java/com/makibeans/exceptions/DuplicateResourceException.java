package com.makibeans.exceptions;

/**
 * Exception thrown when a duplicate resource is detected.
 */

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
