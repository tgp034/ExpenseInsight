package com.expenseinsight.api.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object for dashboard summary information.
 * 
 * <p>Provides a comprehensive overview of the user's financial status including:</p>
 * <ul>
 *   <li>Total income and expenses for a period</li>
 *   <li>Net balance (income - expenses)</li>
 *   <li>Expenses by category breakdown</li>
 *   <li>Budget progress overview</li>
 *   <li>Recent transactions</li>
 * </ul>
 * 
 * <p>Response example:</p>
 * <pre>
 * {
 *   "totalIncome": 3000.00,
 *   "totalExpenses": 2150.50,
 *   "netBalance": 849.50,
 *   "transactionCount": 45,
 *   "expensesByCategory": [...],
 *   "budgetOverview": {...},
 *   "monthlyTrend": [...]
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
public class DashboardSummary {
    
    /**
     * Total income for the period.
     */
    private BigDecimal totalIncome;
    
    /**
     * Total expenses for the period.
     */
    private BigDecimal totalExpenses;
    
    /**
     * Net balance (income - expenses).
     */
    private BigDecimal netBalance;
    
    /**
     * Total number of transactions in the period.
     */
    private Integer transactionCount;
    
    /**
     * Breakdown of expenses by category.
     */
    private List<CategoryExpense> expensesByCategory;
    
    /**
     * Overview of budget status.
     */
    private BudgetOverview budgetOverview;
    
    /**
     * Monthly trend data for charts.
     */
    private List<MonthlyData> monthlyTrend;
}