package com.expenseinsight.api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for user profile information.
 * 
 * <p>Contains complete user profile data excluding sensitive information
 * like passwords.</p>
 * 
 * <p>Response example:</p>
 * <pre>
 * {
 *   "id": 1,
 *   "email": "john.doe@example.com",
 *   "firstName": "John",
 *   "lastName": "Doe",
 *   "fullName": "John Doe",
 *   "isActive": true,
 *   "createdAt": "2024-01-15T10:30:00",
 *   "totalTransactions": 125,
 *   "totalBudgets": 8
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
public class UserProfileResponse {
    
    /**
     * User unique identifier.
     */
    private Long id;
    
    /**
     * User's email address.
     */
    private String email;
    
    /**
     * User's first name.
     */
    private String firstName;
    
    /**
     * User's last name.
     */
    private String lastName;
    
    /**
     * User's full name (first + last).
     */
    private String fullName;
    
    /**
     * Account active status.
     */
    private Boolean isActive;
    
    /**
     * Timestamp when the account was created.
     */
    private LocalDateTime createdAt;
    
    /**
     * Total number of transactions created by user.
     */
    private Integer totalTransactions;
    
    /**
     * Total number of budgets created by user.
     */
    private Integer totalBudgets;
}