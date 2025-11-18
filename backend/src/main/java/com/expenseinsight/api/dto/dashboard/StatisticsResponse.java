package com.expenseinsight.api.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for detailed statistics.
 * 
 * <p>Provides comprehensive statistical analysis including:</p>
 * <ul>
 *   <li>Average daily/weekly/monthly spending</li>
 *   <li>Largest expense</li>
 *   <li>Most frequent category</li>
 *   <li>Savings rate</li>
 * </ul>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    
    /**
     * Average daily spending.
     */
    private BigDecimal averageDailyExpense;
    
    /**
     * Average weekly spending.
     */
    private BigDecimal averageWeeklyExpense;
    
    /**
     * Average monthly spending.
     */
    private BigDecimal averageMonthlyExpense;
    
    /**
     * Largest single expense amount.
     */
    private BigDecimal largestExpense;
    
    /**
     * Description of the largest expense.
     */
    private String largestExpenseDescription;
    
    /**
     * Category with most spending.
     */
    private String topSpendingCategory;
    
    /**
     * Amount spent in top category.
     */
    private BigDecimal topSpendingAmount;
    
    /**
     * Savings rate percentage (income - expenses) / income * 100.
     */
    private Double savingsRate;
}