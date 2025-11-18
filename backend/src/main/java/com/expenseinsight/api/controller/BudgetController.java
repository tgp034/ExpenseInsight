package com.expenseinsight.api.controller;

import com.expenseinsight.api.dto.budget.BudgetResponse;
import com.expenseinsight.api.dto.budget.CreateBudgetRequest;
import com.expenseinsight.api.dto.budget.UpdateBudgetRequest;
import com.expenseinsight.api.security.SecurityUtils;
import com.expenseinsight.api.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for budget management.
 * 
 * <p>Provides endpoints for:</p>
 * <ul>
 *   <li>Creating monthly budgets per category</li>
 *   <li>Retrieving budgets with spending progress</li>
 *   <li>Updating budget amounts</li>
 *   <li>Deleting budgets</li>
 * </ul>
 * 
 * <p>All endpoints require JWT authentication.</p>
 * 
 * <p>Base path: /api/budgets</p>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final SecurityUtils securityUtils;

    /**
     * Creates a new budget for the authenticated user.
     * 
     * <p>Endpoint: POST /api/budgets</p>
     * 
     * <p>Request body example:</p>
     * <pre>
     * {
     *   "categoryId": 1,
     *   "amount": 500.00,
     *   "month": 11,
     *   "year": 2024
     * }
     * </pre>
     * 
     * @param request Budget creation details
     * @return ResponseEntity with created budget (201 Created)
     */
    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(@Valid @RequestBody CreateBudgetRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        BudgetResponse response = budgetService.createBudget(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all budgets for the authenticated user.
     * 
     * <p>Endpoint: GET /api/budgets</p>
     * 
     * @return ResponseEntity with list of budgets (200 OK)
     */
    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getUserBudgets() {
        Long userId = securityUtils.getCurrentUserId();
        List<BudgetResponse> budgets = budgetService.getUserBudgets(userId);
        return ResponseEntity.ok(budgets);
    }

    /**
     * Retrieves budgets for a specific month and year.
     * 
     * <p>Endpoint: GET /api/budgets/period?month={month}&year={year}</p>
     * 
     * <p>Example: GET /api/budgets/period?month=11&year=2024</p>
     * 
     * @param month Month (1-12)
     * @param year Year
     * @return ResponseEntity with list of budgets (200 OK)
     */
    @GetMapping("/period")
    public ResponseEntity<List<BudgetResponse>> getBudgetsByPeriod(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        Long userId = securityUtils.getCurrentUserId();
        List<BudgetResponse> budgets = budgetService.getBudgetsByMonthAndYear(userId, month, year);
        return ResponseEntity.ok(budgets);
    }

    /**
     * Retrieves a specific budget by ID.
     * 
     * <p>Endpoint: GET /api/budgets/{id}</p>
     * 
     * @param id Budget ID
     * @return ResponseEntity with budget details (200 OK)
     */
    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getBudgetById(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        BudgetResponse budget = budgetService.getBudgetById(userId, id);
        return ResponseEntity.ok(budget);
    }

    /**
     * Updates an existing budget amount.
     * 
     * <p>Endpoint: PUT /api/budgets/{id}</p>
     * 
     * <p>Request body example:</p>
     * <pre>
     * {
     *   "amount": 600.00
     * }
     * </pre>
     * 
     * @param id Budget ID to update
     * @param request Updated budget details
     * @return ResponseEntity with updated budget (200 OK)
     */
    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBudgetRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        BudgetResponse response = budgetService.updateBudget(userId, id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a budget.
     * 
     * <p>Endpoint: DELETE /api/budgets/{id}</p>
     * 
     * @param id Budget ID to delete
     * @return ResponseEntity with no content (204 No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        budgetService.deleteBudget(userId, id);
        return ResponseEntity.noContent().build();
    }
}