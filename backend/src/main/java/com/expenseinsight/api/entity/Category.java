package com.expenseinsight.api.entity;

import com.expenseinsight.api.entity.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a transaction category in the ExpenseInsight application.
 * 
 * <p>Categories are used to classify transactions as either expenses or income.
 * The system includes predefined categories (seeded via Flyway migration) but
 * can be extended to support user-defined categories in future versions.</p>
 * 
 * <p>Each category has:</p>
 * <ul>
 *   <li>A unique name (e.g., "Food & Dining", "Salary")</li>
 *   <li>A type (EXPENSE or INCOME)</li>
 *   <li>An optional icon (emoji or icon identifier)</li>
 *   <li>An optional color (hex code for UI representation)</li>
 * </ul>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    /**
     * Unique identifier for the category.
     * Auto-generated using database sequence.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique name of the category.
     * Examples: "Food & Dining", "Transportation", "Salary"
     */
    @NotBlank(message = "Category name is required")
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Type of transactions this category applies to.
     * Can be either EXPENSE or INCOME.
     */
    @NotNull(message = "Category type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    /**
     * Optional icon representation for the category.
     * Can be an emoji (e.g., "🍔") or an icon identifier.
     */
    @Size(max = 50)
    @Column(length = 50)
    private String icon;

    /**
     * Optional hex color code for UI representation.
     * Format: #RRGGBB (e.g., "#FF6B6B")
     */
    @Size(max = 7)
    @Column(length = 7)
    private String color;

    /**
     * Timestamp when the category was created.
     * Set automatically on creation.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * List of all transactions using this category.
     */
    @OneToMany(mappedBy = "category")
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    /**
     * List of all budgets associated with this category.
     */
    @OneToMany(mappedBy = "category")
    @Builder.Default
    private List<Budget> budgets = new ArrayList<>();

    /**
     * JPA lifecycle callback executed before persisting a new category.
     * Sets the createdAt timestamp.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}