package com.expenseinsight.api.dto.budget;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
