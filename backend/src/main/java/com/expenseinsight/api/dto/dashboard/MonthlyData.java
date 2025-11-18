package com.expenseinsight.api.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for monthly trend data.
 * 
 * <p>Used to display charts showing income/expense trends over time.</p>
 * 
 * <p>Example:</p>
 * <pre>
 * {
 *   "month": 11,
 *   "year": 2024,
 *   "monthName": "November",
 *   "income": 3000.00,
 *   "expenses": 2150.50,
 *   "netBalance": 849.50
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
public class MonthlyData {
    
    /**
     * Month number (1-12).
     */
    private Integer month;
    
    /**
     * Year.
     */
    private Integer year;
    
    /**
     * Month name for display (e.g., "January", "February").
     */
    private String monthName;
    
    /**
     * Total income for the month.
     */
    private BigDecimal income;
    
    /**
     * Total expenses for the month.
     */
    private BigDecimal expenses;
    
    /**
     * Net balance for the month (income - expenses).
     */
    private BigDecimal netBalance;
}