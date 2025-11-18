package com.expenseinsight.api.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object for AI-generated weekly summary.
 * 
 * <p>Provides a comprehensive weekly financial summary with AI insights.</p>
 * 
 * <p>Response example:</p>
 * <pre>
 * {
 *   "weekStart": "2024-11-11",
 *   "weekEnd": "2024-11-17",
 *   "totalIncome": 500.00,
 *   "totalExpenses": 380.50,
 *   "netBalance": 119.50,
 *   "topCategories": [...],
 *   "aiSummary": "This week your spending was 15% lower than average...",
 *   "recommendations": [...]
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
public class WeeklySummaryResponse {
    
    /**
     * Start date of the week.
     */
    private String weekStart;
    
    /**
     * End date of the week.
     */
    private String weekEnd;
    
    /**
     * Total income for the week.
     */
    private BigDecimal totalIncome;
    
    /**
     * Total expenses for the week.
     */
    private BigDecimal totalExpenses;
    
    /**
     * Net balance for the week.
     */
    private BigDecimal netBalance;
    
    /**
     * Top spending categories for the week.
     */
    private List<String> topCategories;
    
    /**
     * AI-generated summary of the week's financial activity.
     */
    private String aiSummary;
    
    /**
     * AI-generated recommendations for improvement.
     */
    private List<String> recommendations;
}