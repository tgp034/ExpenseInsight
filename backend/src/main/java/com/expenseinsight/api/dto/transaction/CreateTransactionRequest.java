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
 * Data Transfer Object for creating a new transaction.
 * 
 * <p>This DTO is used when a user registers a new expense or income.
 * The category can be auto-assigned by AI based on the description,
 * or manually selected by the user.</p>
 * 
 * <p>Request example:</p>
 * <pre>
 * {
 *   "amount": 45.50,
 *   "description": "Lunch at Italian restaurant",
 *   "transactionDate": "2024-11-14",
 *   "type": "EXPENSE",
 *   "categoryId": 1,
 *   "paymentMethod": "Credit Card"
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
public class CreateTransactionRequest {
    
    /**
     * Transaction amount.
     * Must be positive and greater than zero.
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    /**
     * Description of the transaction.
     * Used by AI to suggest appropriate category if not provided.
     */
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    /**
     * Date when the transaction occurred.
     * Cannot be in the future.
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
     * If not provided, AI will suggest one based on description.
     */
    @NotNull(message = "Category is required")
    @Positive(message = "Category ID must be positive")
    private Long categoryId;
    
    /**
     * Optional payment method (e.g., "Cash", "Credit Card", "Debit Card").
     */
    @Size(max = 50, message = "Payment method must not exceed 50 characters")
    private String paymentMethod;
}