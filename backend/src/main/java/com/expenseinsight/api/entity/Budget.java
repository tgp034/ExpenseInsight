package com.expenseinsight.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entity representing a budget allocation in the ExpenseInsight application.
 * 
 * <p>A budget defines spending limits for a specific category within a given month and year.
 * Each user can set only one budget per category per month/year combination (enforced by
 * unique constraint).</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Associates a spending limit with a category for budget tracking</li>
 *   <li>Time-bound by month and year for periodic budget management</li>
 *   <li>Prevents duplicate budgets for the same category in the same period</li>
 *   <li>Enables comparison of actual spending vs budgeted amounts</li>
 * </ul>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "budgets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "category_id", "month", "year"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget extends BaseEntity {

    /**
     * Unique identifier for the budget.
     * Auto-generated using database sequence.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who defined this budget.
     * Every budget must be associated with a user.
     */
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The category this budget applies to.
     * Budget limits are set per category to track spending in specific areas.
     */
    @NotNull(message = "Category is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * The budgeted amount for this category.
     * Must be greater than zero. Stored with precision of 12 digits and 2 decimal places.
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /**
     * The month this budget applies to.
     * Valid values are 1 (January) through 12 (December).
     */
    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    @Column(nullable = false)
    private Integer month;

    /**
     * The year this budget applies to.
     * Must be 2020 or later.
     */
    @NotNull(message = "Year is required")
    @Min(value = 2020, message = "Year must be 2020 or later")
    @Column(nullable = false)
    private Integer year;
}