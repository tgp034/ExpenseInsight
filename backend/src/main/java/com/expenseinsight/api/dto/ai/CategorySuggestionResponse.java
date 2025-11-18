package com.expenseinsight.api.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for AI category suggestion responses.
 * 
 * <p>Contains the AI's suggested category and optional comment.</p>
 * 
 * <p>Response example:</p>
 * <pre>
 * {
 *   "suggestedCategoryId": 1,
 *   "suggestedCategoryName": "Food & Dining",
 *   "confidence": 0.95,
 *   "comment": "Fast food restaurant expense, typical lunch cost"
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
public class CategorySuggestionResponse {
    
    /**
     * Suggested category ID.
     */
    private Long suggestedCategoryId;
    
    /**
     * Suggested category name for display.
     */
    private String suggestedCategoryName;
    
    /**
     * Confidence level of the suggestion (0-1).
     */
    private Double confidence;
    
    /**
     * AI-generated comment or insight about the transaction.
     */
    private String comment;
}