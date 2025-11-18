package com.expenseinsight.api.dto.budget;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for budget responses.
 * 
 * <p>Includes budget details, category information, and spending progress.</p>
 * 
 * <p>Response example:</p>
 * <pre>
 * {
 *   "id": 1,
 *   "categoryId": 1,
 *   "categoryName": "Food & Dining",
 *   "categoryIcon": "🍔",
 *   "categoryColor": "#FF6B6B",
 *   "amount": 500.00,
 *   "spent": 345.50,
 *   "remaining": 154.50,
 *   "percentageUsed": 69.1,
 *   "month": 11,
 *   "year": 2024,
 *   "isExceeded": false,
 *   "createdAt": "2024-11-01T10:00:00",
 *   "updatedAt": "2024-11-01T10:00:00"
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
public class BudgetResponse {
    
    /**
     * Budget unique identifier.
     */
    private Long id;
    
    /**
     * Category ID.
     */
    private Long categoryId;
    
    /**
     * Category name for display.
     */
    private String categoryName;
    
    /**
     * Category icon.
     */
    private String categoryIcon;
    
    /**
     * Category color.
     */
    private String categoryColor;
    
    /**
     * Budget limit amount.
     */
    private BigDecimal amount;
    
    /**
     * Amount already spent in this budget period.
     */
    private BigDecimal spent;
    
    /**
     * Remaining budget amount.
     */
    private BigDecimal remaining;
    
    /**
     * Percentage of budget used (0-100+).
     */
    private Double percentageUsed;
    
    /**
     * Month for which the budget applies (1-12).
     */
    private Integer month;
    
    /**
     * Year for which the budget applies.
     */
    private Integer year;
    
    /**
     * Flag indicating if budget has been exceeded.
     */
    private Boolean isExceeded;
    
    /**
     * Timestamp when the budget was created.
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the budget was last updated.
     */
    private LocalDateTime updatedAt;
}