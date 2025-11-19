package com.expenseinsight.api.repository;

import com.expenseinsight.api.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for retrieving and managing {@link Budget} entities.
 *
 * <p>
 * Includes helper methods for loading budgets by user, category, and
 * temporal dimensions to support dashboard and reporting features.
 * </p>
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

        /**
         * Retrieves every budget configured by the specified user.
         *
         * @param userId identifier of the user owning the budgets
         * @return list of budgets belonging to the user
         */
        List<Budget> findByUserId(Long userId);

        /**
         * Counts total budgets configured by the specified user.
         *
         * @param userId identifier of the user owning the budgets
         * @return number of budgets belonging to the user
         */
        long countByUserId(Long userId);

        /**
         * Finds budgets for a user limited to a specific month and year.
         *
         * @param userId identifier of the user owning the budgets
         * @param month  month component of the time window (1-12)
         * @param year   year component of the time window
         * @return list of budgets for the given period
         */
        List<Budget> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);

        /**
         * Obtains a single budget for a user-category pair within a month/year.
         *
         * @param userId     identifier of the user
         * @param categoryId identifier of the category
         * @param month      month component of the budget period
         * @param year       year component of the budget period
         * @return matching budget when present
         */
        Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(
                        Long userId,
                        Long categoryId,
                        Integer month,
                        Integer year);

        /**
         * Determines if a budget already exists for the supplied constraints.
         *
         * @param userId     identifier of the user
         * @param categoryId identifier of the category
         * @param month      month component of the budget period
         * @param year       year component of the budget period
         * @return {@code true} when a budget exists with the given combination
         */
        boolean existsByUserIdAndCategoryIdAndMonthAndYear(
                        Long userId,
                        Long categoryId,
                        Integer month,
                        Integer year);

        /**
         * Fetches all budgets for a user in a specific year ordered by month and
         * category.
         *
         * @param userId identifier of the user
         * @param year   year to filter budgets
         * @return ordered list of budgets for reporting purposes
         */
        @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
                        "AND b.year = :year ORDER BY b.month, b.category.name")
        List<Budget> findUserBudgetsByYear(
                        @Param("userId") Long userId,
                        @Param("year") Integer year);
}