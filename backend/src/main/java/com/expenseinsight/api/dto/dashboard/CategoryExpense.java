package com.expenseinsight.api.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for category-wise expense breakdown.
 * 
 * <p>Used in dashboard to show spending distribution across categories.</p>
 * 
 * <p>Example:</p>
 * <pre>
 * {
 *   "categoryId": 1,
 *   "categoryName": "Food & Dining",
 *   "categoryIcon": "🍔",
 *   "categoryColor": "#FF6B6B",
 *   "totalAmount": 450.50,
 *   "transactionCount": 12,
 *   "percentage": 20.95
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
public class CategoryExpense {
    
    /**
     * Category unique identifier.
     */
    private Long categoryId;
    
    /**
     * Category name.
     */
    private String categoryName;
    
    /**
     * Category icon (emoji or identifier).
     */
    private String categoryIcon;
    
    /**
     * Category color (hex code).
     */
    private String categoryColor;
    
    /**
     * Total amount spent in this category.
     */
    private BigDecimal totalAmount;
    
    /**
     * Number of transactions in this category.
     */
    private Integer transactionCount;
    
    /**
     * Percentage of total expenses (0-100).
     */
    private Double percentage;
}