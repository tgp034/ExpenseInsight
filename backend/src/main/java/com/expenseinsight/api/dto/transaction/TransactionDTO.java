package com.expenseinsight.api.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.expenseinsight.api.entity.enums.TransactionType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private Long categoryId;
    private Long userId;
    private BigDecimal amount;
    private String description;
    private LocalDate transactionDate;
    private TransactionType type;
    private String paymentMethod;
    private String aiComment;
}
