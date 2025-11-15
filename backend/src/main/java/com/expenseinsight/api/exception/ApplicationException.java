package com.expenseinsight.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception class for all application-specific exceptions.
 * 
 * <p>This class provides a consistent way to handle exceptions across the application
 * with associated HTTP status codes and error messages.</p>
 * 
 * <p>Child classes should extend this to create specific exception types
 * (e.g., ResourceNotFoundException, ValidationException, etc.)</p>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Getter
public class ApplicationException extends RuntimeException {
    
    /**
     * HTTP status code to return with this exception.
     */
    private final HttpStatus status;
    
    /**
     * User-friendly error message.
     */
    private final String message;

    /**
     * Constructs a new ApplicationException with a message and HTTP status.
     * 
     * @param message User-friendly error message
     * @param status HTTP status code
     */
    public ApplicationException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }

    /**
     * Constructs a new ApplicationException with a message, HTTP status, and cause.
     * 
     * @param message User-friendly error message
     * @param status HTTP status code
     * @param cause The underlying cause of the exception
     */
    public ApplicationException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.status = status;
    }
}