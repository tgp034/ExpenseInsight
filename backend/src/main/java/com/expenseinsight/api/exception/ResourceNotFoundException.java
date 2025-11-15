package com.expenseinsight.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found.
 * 
 * <p>This exception results in a 404 NOT FOUND HTTP response.</p>
 * 
 * <p>Usage example:</p>
 * <pre>
 * User user = userRepository.findById(id)
 *     .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
 * </pre>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
public class ResourceNotFoundException extends ApplicationException {
    
    /**
     * Constructs a new ResourceNotFoundException with a detail message.
     * 
     * @param message Detail message explaining what resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}