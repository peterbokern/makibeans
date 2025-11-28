package com.makibeans.web;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Minimal, consistent error responses for your own frontend.
 * - Field-level errors for JSON and query params
 * - Clear messages for wrong types (e.g., "expected Long")
 * - Enum errors list allowed values
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1) @RequestBody validation (@Valid on JSON body)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail("One or more fields are invalid.");
        pd.setProperty("timestamp", OffsetDateTime.now());

        List<Map<String, Object>> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(this::toFieldErr).toList();
        pd.setProperty("errors", errors);
        return pd;
    }

    // 2) @ModelAttribute / query param binding errors
    @ExceptionHandler(BindException.class)
    public ProblemDetail handleBindException(BindException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Invalid request parameters");
        pd.setDetail("One or more query parameters are invalid.");
        pd.setProperty("timestamp", OffsetDateTime.now());

        List<Map<String, Object>> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(this::toFieldErr).toList();
        pd.setProperty("errors", errors);
        return pd;
    }

    // 3) Constraint violations (path vars / manual validator)
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Constraint violation");
        pd.setDetail("One or more constraints were violated.");
        pd.setProperty("timestamp", OffsetDateTime.now());

        List<Map<String, Object>> errors = ex.getConstraintViolations()
                .stream().map(this::toViolationErr).toList();
        pd.setProperty("errors", errors);
        return pd;
    }

    // 4) Missing/invalid JSON body, wrong type inside JSON, bad enums, etc.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Malformed or invalid JSON");
        pd.setDetail("The request body is missing or invalid.");
        pd.setProperty("timestamp", OffsetDateTime.now());

        Throwable cause = ex.getCause();
        List<Map<String, Object>> errors = new ArrayList<>();

        if (cause instanceof InvalidFormatException ife) {
            errors.add(jsonTypeError(ife));
        } else if (cause instanceof MismatchedInputException mie) {
            errors.add(jsonMismatchError(mie));
        }
        if (!errors.isEmpty()) pd.setProperty("errors", errors);
        return pd;
    }

    // 5) Wrong type in query/path parameter (e.g., ?page=abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Invalid parameter type");
        pd.setDetail("A parameter has the wrong type.");
        pd.setProperty("timestamp", OffsetDateTime.now());

        Map<String, Object> err = new LinkedHashMap<>();
        err.put("field", ex.getName());
        err.put("rejectedValue", ex.getValue());
        err.put("message", "Expected type: " +
                (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"));
        pd.setProperty("errors", List.of(err));
        return pd;
    }

    // 6) Missing required query parameter
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingParam(MissingServletRequestParameterException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Missing parameter");
        pd.setDetail("A required parameter is missing.");
        pd.setProperty("timestamp", OffsetDateTime.now());

        Map<String, Object> err = new LinkedHashMap<>();
        err.put("field", ex.getParameterName());
        err.put("message", "Parameter is required");
        pd.setProperty("errors", List.of(err));
        return pd;
    }

    // 7) Missing endpoint -> 404 Not Found
    @ExceptionHandler(NoHandlerFoundException.class)
    public ProblemDetail handleNoHandlerFound(NoHandlerFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Not Found");
        pd.setDetail(String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL()));
        pd.setProperty("timestamp", OffsetDateTime.now());
        return pd;
    }

    // 8) HTTP method not allowed -> 405
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.METHOD_NOT_ALLOWED);
        pd.setTitle("Method not allowed");
        String supported = ex.getSupportedMethods() != null ? Arrays.toString(ex.getSupportedMethods()) : "[]";
        pd.setDetail(String.format("Method %s is not supported for this endpoint. Supported: %s", ex.getMethod(), supported));
        pd.setProperty("timestamp", OffsetDateTime.now());
        return pd;
    }

    // 9) Unsupported media type -> 415
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ProblemDetail handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        pd.setTitle("Unsupported media type");
        pd.setDetail(String.format("Content type %s is not supported.", ex.getContentType()));
        pd.setProperty("timestamp", OffsetDateTime.now());
        return pd;
    }

    //handle duplicate resource
    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicateResource(DuplicateResourceException ex) {
        Map<String, Object> error = Map.of(
                "field" , "rawValue",
                "message", ex.getMessage()
        );

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Duplicate resource");
        pd.setDetail(ex.getMessage());
        pd.setProperty("timestamp", OffsetDateTime.now());
        pd.setProperty("errors", List.of(error));
        return pd;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> error = Map.of(
                "field" , "id",
                "message", ex.getMessage()
        );

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Resource not found");
        pd.setDetail(ex.getMessage());
        pd.setProperty("timestamp", OffsetDateTime.now());
        pd.setProperty("errors", List.of(error));
        return pd;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> error = Map.of(
                "field" , "rawValue",
                "message", ex.getMessage()
        );

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail(ex.getMessage());
        pd.setProperty("timestamp", OffsetDateTime.now());
        pd.setProperty("errors", List.of(error));
        return pd;
    }

    //) Fallback
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleOther(Exception ex) {
        // Log the exception so we have a stacktrace in the logs
        log.error("Unhandled exception caught by GlobalExceptionHandler", ex);

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Unexpected error");
        pd.setDetail("Something went wrong.");
        pd.setProperty("timestamp", OffsetDateTime.now());
        return pd;
    }

    // ---------- helpers ----------

    private Map<String, Object> toFieldErr(FieldError fe) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("field", fe.getField());
        m.put("rejectedValue", fe.getRejectedValue());
        m.put("message", fe.getDefaultMessage());
        return m;
    }

    private Map<String, Object> toViolationErr(ConstraintViolation<?> cv) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("field", cv.getPropertyPath() != null ? cv.getPropertyPath().toString() : null);
        m.put("rejectedValue", cv.getInvalidValue());
        m.put("message", cv.getMessage());
        return m;
    }

    private Map<String, Object> jsonTypeError(InvalidFormatException ife) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("field", jsonPath(ife.getPath()));
        m.put("rejectedValue", ife.getValue());
        if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
            Object[] allowed = ife.getTargetType().getEnumConstants();
            m.put("message", "Invalid value. Allowed: " + Arrays.toString(allowed));
        } else {
            m.put("message", "Expected type: " +
                    (ife.getTargetType() != null ? ife.getTargetType().getSimpleName() : "unknown"));
        }
        return m;
    }

    private Map<String, Object> jsonMismatchError(MismatchedInputException mie) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("field", jsonPath(mie.getPath()));
        m.put("message", "Invalid structure or element type");
        return m;
    }

    private String jsonPath(List<JsonMappingException.Reference> path) {
        if (path == null || path.isEmpty()) return null;
        return path.stream()
                .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : "[" + ref.getIndex() + "]")
                .collect(Collectors.joining("."));
    }
}
