package com.expenseinsight.api.dto.category;

import com.expenseinsight.api.entity.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for category responses.
 * 
 * <p>Used to return category information to the frontend
 * for display in transaction forms and filters.</p>
 * 
 * <p>Response example:</p>
 * <pre>
 * {
 *   "id": 1,
 *   "name": "Food & Dining",
 *   "type": "EXPENSE",
 *   "icon": "🍔",
 *   "color": "#FF6B6B"
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
public class CategoryResponse {
    
    /**
     * Category unique identifier.
     */
    private Long id;
    
    /**
     * Category name.
     */
    private String name;
    
    /**
     * Category type (EXPENSE or INCOME).
     */
    private TransactionType type;
    
    /**
     * Category icon (emoji or identifier).
     */
    private String icon;
    
    /**
     * Category color (hex code).
     */
    private String color;
}