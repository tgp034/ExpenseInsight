package com.expenseinsight.api.dto.transaction;

import com.expenseinsight.api.entity.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for transaction responses.
 * 
 * <p>Contains all transaction information including category details
 * and optional AI-generated comments.</p>
 * 
 * <p>Response example:</p>
 * <pre>
 * {
 *   "id": 1,
 *   "amount": 45.50,
 *   "description": "Lunch at Italian restaurant",
 *   "transactionDate": "2024-11-14",
 *   "type": "EXPENSE",
 *   "paymentMethod": "Credit Card",
 *   "categoryId": 1,
 *   "categoryName": "Food & Dining",
 *   "categoryIcon": "🍔",
 *   "categoryColor": "#FF6B6B",
 *   "aiComment": "Regular dining expense, within budget",
 *   "createdAt": "2024-11-14T10:30:00",
 *   "updatedAt": "2024-11-14T10:30:00"
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
public class TransactionResponse {
    
    /**
     * Transaction unique identifier.
     */
    private Long id;
    
    /**
     * Transaction amount.
     */
    private BigDecimal amount;
    
    /**
     * Description of the transaction.
     */
    private String description;
    
    /**
     * Date when the transaction occurred.
     */
    private LocalDate transactionDate;
    
    /**
     * Type of transaction (EXPENSE or INCOME).
     */
    private TransactionType type;
    
    /**
     * Payment method used.
     */
    private String paymentMethod;
    
    /**
     * Category ID.
     */
    private Long categoryId;
    
    /**
     * Category name for display.
     */
    private String categoryName;
    
    /**
     * Category icon (emoji or identifier).
     */
    private String categoryIcon;
    
    /**
     * Category color (hex code).
     */
    private String categoryColor;
    
    /**
     * Optional AI-generated comment or insight about the transaction.
     */
    private String aiComment;
    
    /**
     * Timestamp when the transaction was created.
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the transaction was last updated.
     */
    private LocalDateTime updatedAt;
}