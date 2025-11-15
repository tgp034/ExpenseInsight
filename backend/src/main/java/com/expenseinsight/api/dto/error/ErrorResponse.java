package com.expenseinsight.api.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error response format for all API errors.
 * 
 * <p>This DTO provides a consistent structure for error responses across the API,
 * making it easier for frontend applications to handle errors uniformly.</p>
 * 
 * <p>Response structure:</p>
 * <pre>
 * {
 *   "timestamp": "2024-11-14T10:30:00",
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "Validation failed",
 *   "path": "/api/auth/register",
 *   "errors": ["Email is required", "Password must be at least 8 characters"]
 * }
 * </pre>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    /**
     * Timestamp when the error occurred.
     */
    private LocalDateTime timestamp;
    
    /**
     * HTTP status code (e.g., 400, 404, 500).
     */
    private int status;
    
    /**
     * HTTP status text (e.g., "Bad Request", "Not Found").
     */
    private String error;
    
    /**
     * Main error message describing what went wrong.
     */
    private String message;
    
    /**
     * Request path where the error occurred.
     */
    private String path;
    
    /**
     * Optional list of detailed error messages (e.g., validation errors).
     * Only included if there are multiple errors.
     */
    private List<String> errors;
}