package com.makibeans.exceptions;

/**
 * Exception thrown when a circular reference is detected.
 */

public class CircularReferenceException extends RuntimeException {
    public CircularReferenceException(String message) {
        super(message);
    }
}
