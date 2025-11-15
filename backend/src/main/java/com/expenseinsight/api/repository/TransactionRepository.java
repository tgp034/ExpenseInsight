package com.expenseinsight.api.repository;

import com.expenseinsight.api.entity.Transaction;
import com.expenseinsight.api.entity.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


/**
 * Repository for {@link Transaction} entities.
 *
 * <p>Exposes common queries used by the dashboard and transaction listing
 * endpoints. Includes convenience finder methods and custom JPQL queries
 * for date-range and aggregation operations.</p>
 *
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Returns all transactions for a user ordered by transaction date descending.
     *
     * @param userId id of the user
     * @return list of transactions ordered newest first
     */
    List<Transaction> findByUserIdOrderByTransactionDateDesc(Long userId);

    /**
     * Returns transactions for a user filtered by transaction type (EXPENSE/INCOME).
     *
     * @param userId id of the user
     * @param type   the {@link TransactionType} to filter by
     * @return list of transactions matching the type
     */
    List<Transaction> findByUserIdAndType(Long userId, TransactionType type);

    /**
     * Returns transactions for a user that occurred between two dates (inclusive).
     *
     * @param userId    id of the user
     * @param startDate start of the date range (inclusive)
     * @param endDate   end of the date range (inclusive)
     * @return list of transactions within the range
     */
    List<Transaction> findByUserIdAndTransactionDateBetween(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Returns transactions for a user that belong to a specific category.
     *
     * @param userId     id of the user
     * @param categoryId id of the category
     * @return list of transactions for the category
     */
    List<Transaction> findByUserIdAndCategoryId(Long userId, Long categoryId);

    /**
     * Custom query that returns transactions for a user within a date range,
     * ordered by transaction date descending.
     *
     * @param userId    id of the user
     * @param startDate start of the date range
     * @param endDate   end of the date range
     * @return list of transactions matching criteria
     */
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate " +
            "ORDER BY t.transactionDate DESC")
    List<Transaction> findUserTransactionsByDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Aggregates (sums) the transaction amounts for a user filtered by type
     * and date range. May return {@code null} when no matching transactions
     * are present.
     *
     * @param userId    id of the user
     * @param type      transaction type to aggregate
     * @param startDate start of the date range
     * @param endDate   end of the date range
     * @return the summed amount or {@code null} when no matching rows
     */
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId " +
            "AND t.type = :type AND t.transactionDate BETWEEN :startDate AND :endDate")
    Double getTotalAmountByUserAndTypeAndDateRange(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}