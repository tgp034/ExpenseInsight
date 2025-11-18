package com.expenseinsight.api.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for budget overview in dashboard.
 * 
 * <p>Provides aggregate information about all budgets for the current period.</p>
 * 
 * <p>Example:</p>
 * <pre>
 * {
 *   "totalBudgeted": 2000.00,
 *   "totalSpent": 1650.50,
 *   "totalRemaining": 349.50,
 *   "percentageUsed": 82.53,
 *   "budgetsExceeded": 2,
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
public class BudgetOverview {
    
    /**
     * Total amount budgeted across all categories.
     */
    private BigDecimal totalBudgeted;
    
    /**
     * Total amount spent across all budgeted categories.
     */
    private BigDecimal totalSpent;
    
    /**
     * Total remaining budget across all categories.
     */
    private BigDecimal totalRemaining;
    
    /**
     * Overall percentage of budget used (0-100+).
     */
    private Double percentageUsed;
    
    /**
     * Number of budgets that have been exceeded.
     */
    private Integer budgetsExceeded;
    
    /**
     * Total number of active budgets.
     */
    private Integer totalBudgets;
}