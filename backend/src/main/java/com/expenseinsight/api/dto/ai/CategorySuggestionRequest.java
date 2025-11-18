package com.expenseinsight.api.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for requesting AI category suggestions.
 * 
 * <p>Used to send transaction details to AI for automatic categorization.</p>
 * 
 * <p>Request example:</p>
 * <pre>
 * {
 *   "description": "Lunch at McDonald's",
 *   "amount": 12.50
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
public class CategorySuggestionRequest {
    
    /**
     * Transaction description to analyze.
     */
    @NotBlank(message = "Description is required")
    private String description;
    
    /**
     * Transaction amount (helps with context).
     */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}