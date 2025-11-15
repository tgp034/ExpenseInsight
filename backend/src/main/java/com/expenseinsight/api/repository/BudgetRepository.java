package com.expenseinsight.api.repository;

import com.expenseinsight.api.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserId(Long userId);

    List<Budget> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);

    Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(
            Long userId,
            Long categoryId,
            Integer month,
            Integer year
    );

    boolean existsByUserIdAndCategoryIdAndMonthAndYear(
            Long userId,
            Long categoryId,
            Integer month,
            Integer year
    );

    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
            "AND b.year = :year ORDER BY b.month, b.category.name")
    List<Budget> findUserBudgetsByYear(
            @Param("userId") Long userId,
            @Param("year") Integer year
    );
}