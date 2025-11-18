package com.expenseinsight.api.service;

import com.expenseinsight.api.dto.budget.BudgetResponse;
import com.expenseinsight.api.dto.budget.CreateBudgetRequest;
import com.expenseinsight.api.dto.budget.UpdateBudgetRequest;
import com.expenseinsight.api.entity.Budget;
import com.expenseinsight.api.entity.Category;
import com.expenseinsight.api.entity.User;
import com.expenseinsight.api.entity.enums.TransactionType;
import com.expenseinsight.api.exception.DuplicateResourceException;
import com.expenseinsight.api.exception.ResourceNotFoundException;
import com.expenseinsight.api.repository.BudgetRepository;
import com.expenseinsight.api.repository.CategoryRepository;
import com.expenseinsight.api.repository.TransactionRepository;
import com.expenseinsight.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing budgets.
 * 
 * <p>Provides business logic for:</p>
 * <ul>
 *   <li>Creating and managing monthly budgets per category</li>
 *   <li>Calculating budget usage and remaining amounts</li>
 *   <li>Detecting budget overruns</li>
 *   <li>Retrieving budget summaries</li>
 * </ul>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Creates a new budget for a user.
     * 
     * <p>Validates that:</p>
     * <ul>
     *   <li>User exists</li>
     *   <li>Category exists</li>
     *   <li>No duplicate budget exists for the same category/month/year</li>
     * </ul>
     * 
     * @param userId ID of the user creating the budget
     * @param request Budget creation details
     * @return Created budget with spending information
     * @throws ResourceNotFoundException if user or category not found
     * @throws DuplicateResourceException if budget already exists
     */
    @Transactional
    public BudgetResponse createBudget(Long userId, CreateBudgetRequest request) {
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        // Check for duplicate budget
        if (budgetRepository.existsByUserIdAndCategoryIdAndMonthAndYear(
                userId, request.getCategoryId(), request.getMonth(), request.getYear())) {
            throw new DuplicateResourceException(
                    "Budget already exists for this category in " + request.getMonth() + "/" + request.getYear());
        }

        // Create budget entity
        Budget budget = Budget.builder()
                .user(user)
                .category(category)
                .amount(request.getAmount())
                .month(request.getMonth())
                .year(request.getYear())
                .build();

        // Save budget
        Budget savedBudget = budgetRepository.save(budget);

        // Return with spending information
        return mapToResponse(savedBudget);
    }

    /**
     * Retrieves all budgets for a user.
     * 
     * @param userId ID of the user
     * @return List of budgets with spending information
     */
    @Transactional(readOnly = true)
    public List<BudgetResponse> getUserBudgets(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        List<Budget> budgets = budgetRepository.findByUserId(userId);
        return budgets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves budgets for a specific month and year.
     * 
     * @param userId ID of the user
     * @param month Month (1-12)
     * @param year Year
     * @return List of budgets for the specified period
     */
    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgetsByMonthAndYear(Long userId, Integer month, Integer year) {
        List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndYear(userId, month, year);
        return budgets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific budget by ID.
     * 
     * @param userId ID of the user
     * @param budgetId ID of the budget
     * @return Budget details with spending information
     * @throws ResourceNotFoundException if budget not found or doesn't belong to user
     */
    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + budgetId));

        // Verify budget belongs to user
        if (!budget.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Budget not found with id: " + budgetId);
        }

        return mapToResponse(budget);
    }

    /**
     * Updates an existing budget amount.
     * 
     * @param userId ID of the user
     * @param budgetId ID of the budget to update
     * @param request Updated budget details
     * @return Updated budget with spending information
     * @throws ResourceNotFoundException if budget not found or doesn't belong to user
     */
    @Transactional
    public BudgetResponse updateBudget(Long userId, Long budgetId, UpdateBudgetRequest request) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + budgetId));

        // Verify budget belongs to user
        if (!budget.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Budget not found with id: " + budgetId);
        }

        // Update amount
        budget.setAmount(request.getAmount());

        Budget updatedBudget = budgetRepository.save(budget);
        return mapToResponse(updatedBudget);
    }

    /**
     * Deletes a budget.
     * 
     * @param userId ID of the user
     * @param budgetId ID of the budget to delete
     * @throws ResourceNotFoundException if budget not found or doesn't belong to user
     */
    @Transactional
    public void deleteBudget(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + budgetId));

        // Verify budget belongs to user
        if (!budget.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Budget not found with id: " + budgetId);
        }

        budgetRepository.delete(budget);
    }

    /**
     * Maps a Budget entity to a BudgetResponse DTO with spending calculations.
     * 
     * <p>Calculates:</p>
     * <ul>
     *   <li>Total spent in the budget period</li>
     *   <li>Remaining budget</li>
     *   <li>Percentage used</li>
     *   <li>Whether budget is exceeded</li>
     * </ul>
     * 
     * @param budget Budget entity
     * @return BudgetResponse DTO with complete information
     */
    private BudgetResponse mapToResponse(Budget budget) {
        // Calculate date range for the budget period
        YearMonth yearMonth = YearMonth.of(budget.getYear(), budget.getMonth());
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Calculate total spent in this category during the budget period
        // Use DB-side aggregation for precision and performance. The
        // repository method may return {@code null} when there are no matching
        // transactions, so default to {@link BigDecimal#ZERO} in that case.
        BigDecimal spent = transactionRepository
                .getTotalAmountByUserAndTypeAndCategoryAndDateRange(
                        budget.getUser().getId(),
                        budget.getCategory().getId(),
                        TransactionType.EXPENSE,
                        startDate,
                        endDate
                );

        if (spent == null) {
            spent = BigDecimal.ZERO;
        }

        // Calculate remaining and percentage
        // Protect against null budget amount or division by zero
        BigDecimal budgetAmount = budget.getAmount() == null ? BigDecimal.ZERO : budget.getAmount();
        BigDecimal remaining = budgetAmount.subtract(spent);

        Double percentageUsed;
        if (budgetAmount.compareTo(BigDecimal.ZERO) == 0) {
            percentageUsed = 0.0;
        } else {
            percentageUsed = spent.divide(budgetAmount, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        Boolean isExceeded = spent.compareTo(budgetAmount) > 0;

        return BudgetResponse.builder()
                .id(budget.getId())
                .categoryId(budget.getCategory().getId())
                .categoryName(budget.getCategory().getName())
                .categoryIcon(budget.getCategory().getIcon())
                .categoryColor(budget.getCategory().getColor())
                .amount(budget.getAmount())
                .spent(spent)
                .remaining(remaining)
                .percentageUsed(percentageUsed)
                .month(budget.getMonth())
                .year(budget.getYear())
                .isExceeded(isExceeded)
                .createdAt(budget.getCreatedAt())
                .updatedAt(budget.getUpdatedAt())
                .build();
    }
}