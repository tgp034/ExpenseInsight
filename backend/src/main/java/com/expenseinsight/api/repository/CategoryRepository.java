package com.expenseinsight.api.repository;

import com.expenseinsight.api.entity.Category;
import com.expenseinsight.api.entity.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Category} entities.
 *
 * <p>Exposes methods to load categories by type and name. Useful for seeding,
 * UI dropdowns and validation when creating budgets or transactions.</p>
 *
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds all categories of the given transaction type (EXPENSE or INCOME).
     *
     * @param type the {@link TransactionType} to filter categories
     * @return list of categories matching the type
     */
    List<Category> findByType(TransactionType type);

    /**
     * Finds a category by its unique name.
     *
     * @param name category name to search for
     * @return Optional containing the Category when found
     */
    Optional<Category> findByName(String name);

    /**
     * Checks if a category with the provided name already exists.
     *
     * @param name category name to check
     * @return {@code true} when a category with the name exists
     */
    boolean existsByName(String name);
}