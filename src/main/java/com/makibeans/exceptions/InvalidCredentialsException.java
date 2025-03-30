package com.makibeans.exceptions;

/**
 * Exception thrown when invalid credentials are provided.
 */

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
