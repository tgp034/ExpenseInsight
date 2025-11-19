package com.expenseinsight.api.service;

import com.expenseinsight.api.dto.budget.BudgetResponse;
import com.expenseinsight.api.dto.dashboard.*;
import com.expenseinsight.api.entity.Transaction;
import com.expenseinsight.api.entity.enums.TransactionType;
import com.expenseinsight.api.exception.ResourceNotFoundException;
import com.expenseinsight.api.repository.TransactionRepository;
import com.expenseinsight.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for dashboard and statistics operations.
 * 
 * <p>
 * Provides comprehensive financial analysis including:
 * </p>
 * <ul>
 * <li>Summary dashboards with key metrics</li>
 * <li>Category-wise expense breakdowns</li>
 * <li>Budget tracking and alerts</li>
 * <li>Monthly trends and comparisons</li>
 * <li>Statistical analysis of spending patterns</li>
 * </ul>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BudgetService budgetService;

    /**
     * Generates a comprehensive dashboard summary for a specific period.
     * 
     * <p>
     * Includes:
     * </p>
     * <ul>
     * <li>Income, expenses, and net balance</li>
     * <li>Expense breakdown by category</li>
     * <li>Budget overview</li>
     * <li>Monthly trend for the last 6 months</li>
     * </ul>
     * 
     * @param userId    ID of the user
     * @param startDate Start date of the period
     * @param endDate   End date of the period
     * @return Dashboard summary with all metrics
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public DashboardSummary getDashboardSummary(Long userId, LocalDate startDate, LocalDate endDate) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        // Get all transactions for the period
        List<Transaction> transactions = transactionRepository
                .findUserTransactionsByDateRange(userId, startDate, endDate);

        // Calculate totals
        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        // Get expenses by category
        List<CategoryExpense> expensesByCategory = getExpensesByCategory(transactions, totalExpenses);

        // Get budget overview for current month
        LocalDate now = LocalDate.now();
        BudgetOverview budgetOverview = getBudgetOverview(userId, now.getMonthValue(), now.getYear());

        // Get monthly trend (last 6 months)
        List<MonthlyData> monthlyTrend = getMonthlyTrend(userId, 6);

        return DashboardSummary.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .transactionCount(transactions.size())
                .expensesByCategory(expensesByCategory)
                .budgetOverview(budgetOverview)
                .monthlyTrend(monthlyTrend)
                .build();
    }

    /**
     * Generates detailed statistics for a user's spending patterns.
     * 
     * @param userId    ID of the user
     * @param startDate Start date of analysis period
     * @param endDate   End date of analysis period
     * @return Detailed statistics response
     */
    @Transactional(readOnly = true)
    public StatisticsResponse getStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository
                .findUserTransactionsByDateRange(userId, startDate, endDate);

        List<Transaction> expenses = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.toList());

        List<Transaction> income = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .collect(Collectors.toList());

        // Calculate totals
        BigDecimal totalExpenses = expenses.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalIncome = income.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate days in period
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // Calculate averages
        BigDecimal averageDailyExpense = days > 0
                ? totalExpenses.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal averageWeeklyExpense = averageDailyExpense.multiply(BigDecimal.valueOf(7));
        BigDecimal averageMonthlyExpense = averageDailyExpense.multiply(BigDecimal.valueOf(30));

        // Find largest expense
        Transaction largestExpenseTransaction = expenses.stream()
                .max(Comparator.comparing(Transaction::getAmount))
                .orElse(null);

        // Find top spending category
        Map<String, BigDecimal> categoryTotals = expenses.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));

        Map.Entry<String, BigDecimal> topCategory = categoryTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        // Calculate savings rate
        Double savingsRate = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? totalIncome.subtract(totalExpenses)
                        .divide(totalIncome, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue()
                : 0.0;

        return StatisticsResponse.builder()
                .averageDailyExpense(averageDailyExpense)
                .averageWeeklyExpense(averageWeeklyExpense)
                .averageMonthlyExpense(averageMonthlyExpense)
                .largestExpense(
                        largestExpenseTransaction != null ? largestExpenseTransaction.getAmount() : BigDecimal.ZERO)
                .largestExpenseDescription(
                        largestExpenseTransaction != null ? largestExpenseTransaction.getDescription() : "N/A")
                .topSpendingCategory(topCategory != null ? topCategory.getKey() : "N/A")
                .topSpendingAmount(topCategory != null ? topCategory.getValue() : BigDecimal.ZERO)
                .savingsRate(savingsRate)
                .build();
    }

    /**
     * Calculates expense breakdown by category.
     * 
     * @param transactions  List of transactions to analyze
     * @param totalExpenses Total expense amount for percentage calculation
     * @return List of category expenses with percentages
     */
    private List<CategoryExpense> getExpensesByCategory(List<Transaction> transactions, BigDecimal totalExpenses) {
        // Group expenses by category
        Map<Long, List<Transaction>> groupedByCategory = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(t -> t.getCategory().getId()));

        return groupedByCategory.entrySet().stream()
                .map(entry -> {
                    List<Transaction> categoryTransactions = entry.getValue();
                    Transaction sample = categoryTransactions.get(0);

                    BigDecimal categoryTotal = categoryTransactions.stream()
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    Double percentage = totalExpenses.compareTo(BigDecimal.ZERO) > 0
                            ? categoryTotal.divide(totalExpenses, 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100))
                                    .doubleValue()
                            : 0.0;

                    return CategoryExpense.builder()
                            .categoryId(sample.getCategory().getId())
                            .categoryName(sample.getCategory().getName())
                            .categoryIcon(sample.getCategory().getIcon())
                            .categoryColor(sample.getCategory().getColor())
                            .totalAmount(categoryTotal)
                            .transactionCount(categoryTransactions.size())
                            .percentage(percentage)
                            .build();
                })
                .sorted(Comparator.comparing(CategoryExpense::getTotalAmount).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Generates budget overview for a specific month.
     * 
     * @param userId ID of the user
     * @param month  Month (1-12)
     * @param year   Year
     * @return Budget overview with aggregate information
     */
    private BudgetOverview getBudgetOverview(Long userId, Integer month, Integer year) {
        List<BudgetResponse> budgets = budgetService.getBudgetsByMonthAndYear(userId, month, year);

        if (budgets.isEmpty()) {
            return BudgetOverview.builder()
                    .totalBudgeted(BigDecimal.ZERO)
                    .totalSpent(BigDecimal.ZERO)
                    .totalRemaining(BigDecimal.ZERO)
                    .percentageUsed(0.0)
                    .budgetsExceeded(0)
                    .totalBudgets(0)
                    .build();
        }

        BigDecimal totalBudgeted = budgets.stream()
                .map(BudgetResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSpent = budgets.stream()
                .map(BudgetResponse::getSpent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRemaining = budgets.stream()
                .map(BudgetResponse::getRemaining)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Double percentageUsed = totalBudgeted.compareTo(BigDecimal.ZERO) > 0
                ? totalSpent.divide(totalBudgeted, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue()
                : 0.0;

        Integer budgetsExceeded = (int) budgets.stream()
                .filter(BudgetResponse::getIsExceeded)
                .count();

        return BudgetOverview.builder()
                .totalBudgeted(totalBudgeted)
                .totalSpent(totalSpent)
                .totalRemaining(totalRemaining)
                .percentageUsed(percentageUsed)
                .budgetsExceeded(budgetsExceeded)
                .totalBudgets(budgets.size())
                .build();
    }

    /**
     * Generates monthly trend data for the last N months.
     * 
     * @param userId     ID of the user
     * @param monthsBack Number of months to include
     * @return List of monthly data points
     */
    private List<MonthlyData> getMonthlyTrend(Long userId, int monthsBack) {
        List<MonthlyData> monthlyData = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = monthsBack - 1; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(today.minusMonths(i));
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();

            List<Transaction> transactions = transactionRepository
                    .findUserTransactionsByDateRange(userId, startDate, endDate);

            BigDecimal income = transactions.stream()
                    .filter(t -> t.getType() == TransactionType.INCOME)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal expenses = transactions.stream()
                    .filter(t -> t.getType() == TransactionType.EXPENSE)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            monthlyData.add(MonthlyData.builder()
                    .month(yearMonth.getMonthValue())
                    .year(yearMonth.getYear())
                    .monthName(yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                    .income(income)
                    .expenses(expenses)
                    .netBalance(income.subtract(expenses))
                    .build());
        }

        return monthlyData;
    }
}