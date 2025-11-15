package com.expenseinsight.api.dto.budget;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for budget information.
 * 
 * <p>Used to transfer budget data between the API layer and clients.
 * Contains budget allocation details including amount, category, and time period.</p>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDTO {
    private Long id;
    private Long userId;
    private Long categoryId;
    private Double amount;
    private Integer month;
    private Integer year;
}
