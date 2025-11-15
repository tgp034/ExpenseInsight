package com.expenseinsight.api.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.expenseinsight.api.entity.enums.TransactionType;

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
