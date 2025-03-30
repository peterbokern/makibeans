package com.makibeans.exceptions;

/**
 * Exception thrown when an error occurs during image processing.
 */

public class ImageProcessingException extends RuntimeException {
    public ImageProcessingException(String message) {
        super(message);
    }

    public ImageProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}

