package com.expenseinsight.api.dto.transaction;

import com.expenseinsight.api.entity.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for updating an existing transaction.
 * 
 * <p>Allows users to modify transaction details after creation.</p>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTransactionRequest {
    
    /**
     * Transaction amount.
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    /**
     * Description of the transaction.
     */
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    /**
     * Date when the transaction occurred.
     */
    @NotNull(message = "Transaction date is required")
    @PastOrPresent(message = "Transaction date cannot be in the future")
    private LocalDate transactionDate;
    
    /**
     * Type of transaction (EXPENSE or INCOME).
     */
    @NotNull(message = "Transaction type is required")
    private TransactionType type;
    
    /**
     * Category ID for the transaction.
     */
    @NotNull(message = "Category is required")
    @Positive(message = "Category ID must be positive")
    private Long categoryId;
    
    /**
     * Payment method.
     */
    @Size(max = 50, message = "Payment method must not exceed 50 characters")
    private String paymentMethod;
}