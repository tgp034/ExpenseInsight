package com.expenseinsight.api.exception;

import com.expenseinsight.api.dto.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for the ExpenseInsight API.
 * 
 * <p>This class intercepts exceptions thrown by controllers and converts them
 * into standardized error responses. It ensures that all errors are returned
 * in a consistent JSON format.</p>
 * 
 * <p>Handles the following exception types:</p>
 * <ul>
 *   <li>Application-specific exceptions (ResourceNotFoundException, etc.)</li>
 *   <li>Validation errors (MethodArgumentNotValidException)</li>
 *   <li>Authentication errors (BadCredentialsException, UsernameNotFoundException)</li>
 *   <li>Generic exceptions (catch-all for unexpected errors)</li>
 * </ul>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles all application-specific exceptions.
     * 
     * <p>These exceptions already contain the appropriate HTTP status code
     * and error message.</p>
     * 
     * @param ex The application exception
     * @param request HTTP request where the exception occurred
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(
            ApplicationException ex,
            HttpServletRequest request) {
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getStatus().value())
                .error(ex.getStatus().getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    /**
     * Handles validation errors from @Valid annotations.
     * 
     * <p>Collects all field validation errors and returns them as a list
     * in the error response.</p>
     * 
     * <p>Example response:</p>
     * <pre>
     * {
     *   "status": 400,
     *   "error": "Bad Request",
     *   "message": "Validation failed",
     *   "errors": [
     *     "email: Email is required",
     *     "password: Password must be at least 8 characters"
     *   ]
     * }
     * </pre>
     * 
     * @param ex The validation exception
     * @param request HTTP request where the exception occurred
     * @return ResponseEntity with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        // Extract all field errors
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String message = error.getDefaultMessage();
                    return fieldName + ": " + message;
                })
                .collect(Collectors.toList());
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .path(request.getRequestURI())
                .errors(errors)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles authentication failures (invalid credentials).
     * 
     * <p>Triggered when Spring Security authentication fails during login.</p>
     * 
     * @param ex The bad credentials exception
     * @param request HTTP request where the exception occurred
     * @return ResponseEntity with authentication error
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex,
            HttpServletRequest request) {
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message("Invalid email or password")
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handles user not found exceptions during authentication.
     * 
     * @param ex The username not found exception
     * @param request HTTP request where the exception occurred
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
            UsernameNotFoundException ex,
            HttpServletRequest request) {
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message("Invalid email or password")
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handles all other unexpected exceptions.
     * 
     * <p>This is a catch-all handler for any exception not specifically handled above.
     * Returns a generic 500 Internal Server Error response.</p>
     * 
     * <p>In production, consider logging these exceptions for debugging.</p>
     * 
     * @param ex The unexpected exception
     * @param request HTTP request where the exception occurred
     * @return ResponseEntity with generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        // Log the exception (important for debugging)
        ex.printStackTrace();
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected error occurred. Please try again later.")
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}