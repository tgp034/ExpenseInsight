package com.expenseinsight.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication fails.
 * 
 * <p>This exception results in a 401 UNAUTHORIZED HTTP response.</p>
 * 
 * <p>Typical use cases:</p>
 * <ul>
 *   <li>Invalid login credentials</li>
 *   <li>Expired or invalid JWT token</li>
 *   <li>User account is inactive</li>
 * </ul>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
public class UnauthorizedException extends ApplicationException {
    
    /**
     * Constructs a new UnauthorizedException with a detail message.
     * 
     * @param message Detail message explaining the authentication failure
     */
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}