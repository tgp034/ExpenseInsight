package com.expenseinsight.api.entity;

import com.expenseinsight.api.entity.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing a financial transaction in the ExpenseInsight application.
 * 
 * <p>A transaction represents either an income or expense entry associated with a user.
 * Each transaction is categorized and includes details about amount, date, and optional
 * payment method and AI-generated insights.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Linked to a specific user and category</li>
 *   <li>Classified as either EXPENSE or INCOME</li>
 *   <li>Supports AI-generated comments for enhanced insights</li>
 *   <li>Tracks payment method for expense management</li>
 * </ul>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseEntity {

    /**
     * Unique identifier for the transaction.
     * Auto-generated using database sequence.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who owns this transaction.
     * Every transaction must be associated with a user.
     */
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The category this transaction belongs to.
     * Helps classify the transaction as a specific type of expense or income.
     */
    @NotNull(message = "Category is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * The monetary amount of the transaction.
     * Must be greater than zero. Stored with precision of 12 digits and 2 decimal places.
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /**
     * Description or note about the transaction.
     * Provides context and details about what the transaction was for.
     */
    @NotBlank(message = "Description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * The date when the transaction occurred.
     * This is the actual transaction date, not the creation date in the system.
     */
    @NotNull(message = "Transaction date is required")
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    /**
     * The type of transaction.
     * Can be either EXPENSE or INCOME.
     */
    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    /**
     * Optional payment method used for the transaction.
     * Examples: "Credit Card", "Cash", "Bank Transfer", "PayPal"
     */
    @Size(max = 50)
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    /**
     * Optional AI-generated comment or insight about the transaction.
     * Provides automated analysis or suggestions related to spending patterns.
     */
    @Column(name = "ai_comment", columnDefinition = "TEXT")
    private String aiComment;
}