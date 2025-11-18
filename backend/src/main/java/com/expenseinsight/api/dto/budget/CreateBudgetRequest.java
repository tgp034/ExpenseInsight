package com.expenseinsight.api.dto.budget;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for creating a new budget.
 * 
 * <p>Budgets are defined per category, month, and year. Users can set
 * spending limits for specific categories to help control their expenses.</p>
 * 
 * <p>Request example:</p>
 * <pre>
 * {
 *   "categoryId": 1,
 *   "amount": 500.00,
 *   "month": 11,
 *   "year": 2024
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
public class CreateBudgetRequest {
    
    /**
     * Category ID for which the budget is being set.
     */
    @NotNull(message = "Category is required")
    @Positive(message = "Category ID must be positive")
    private Long categoryId;
    
    /**
     * Budget amount limit for the category.
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    /**
     * Month for which the budget applies (1-12).
     */
    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;
    
    /**
     * Year for which the budget applies.
     */
    @NotNull(message = "Year is required")
    @Min(value = 2020, message = "Year must be 2020 or later")
    private Integer year;
}