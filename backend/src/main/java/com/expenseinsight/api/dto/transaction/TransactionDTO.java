package com.expenseinsight.api.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.expenseinsight.api.entity.enums.TransactionType;

/**
 * Data Transfer Object for transaction information.
 * 
 * <p>Used to transfer transaction data between the API layer and clients.
 * Contains all transaction details including amount, description, category,
 * and optional AI-generated insights.</p>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
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
