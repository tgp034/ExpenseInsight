package com.expenseinsight.api.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.expenseinsight.api.entity.enums.TransactionType;

/**
 * Data Transfer Object for category information.
 * 
 * <p>Used to transfer category data between the API layer and clients.
 * Contains category details including name, type, icon, and color for UI representation.</p>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private TransactionType type;
    private String icon;
    private String color;
    private LocalDateTime createdAt;
}
