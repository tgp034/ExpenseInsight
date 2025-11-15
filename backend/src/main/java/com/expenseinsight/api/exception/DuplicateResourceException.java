package com.expenseinsight.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to create a resource that already exists.
 * 
 * <p>This exception results in a 409 CONFLICT HTTP response.</p>
 * 
 * <p>Typical use cases:</p>
 * <ul>
 *   <li>Registering a user with an email that already exists</li>
 *   <li>Creating a budget that already exists for the same month/year/category</li>
 * </ul>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
public class DuplicateResourceException extends ApplicationException {
    
    /**
     * Constructs a new DuplicateResourceException with a detail message.
     * 
     * @param message Detail message explaining what resource is duplicated
     */
    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}