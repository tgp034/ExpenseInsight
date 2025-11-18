package com.expenseinsight.api.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating user profile information.
 * 
 * <p>Allows users to update their first and last names.
 * Email cannot be changed to prevent authentication issues.</p>
 * 
 * <p>Request example:</p>
 * <pre>
 * {
 *   "firstName": "John",
 *   "lastName": "Smith"
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
public class UpdateProfileRequest {
    
    /**
     * Updated first name.
     */
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;
    
    /**
     * Updated last name.
     */
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;
}