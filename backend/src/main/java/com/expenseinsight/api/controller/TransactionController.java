package com.expenseinsight.api.controller;

import com.expenseinsight.api.dto.transaction.CreateTransactionRequest;
import com.expenseinsight.api.dto.transaction.TransactionResponse;
import com.expenseinsight.api.dto.transaction.UpdateTransactionRequest;
import com.expenseinsight.api.entity.enums.TransactionType;
import com.expenseinsight.api.security.SecurityUtils;
import com.expenseinsight.api.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for transaction management.
 * 
 * <p>Provides endpoints for:</p>
 * <ul>
 *   <li>Creating new transactions</li>
 *   <li>Retrieving transactions (all, by ID, by type, by date range)</li>
 *   <li>Updating transactions</li>
 *   <li>Deleting transactions</li>
 * </ul>
 * 
 * <p>All endpoints require JWT authentication.</p>
 * 
 * <p>Base path: /api/transactions</p>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final SecurityUtils securityUtils;

    /**
     * Creates a new transaction for the authenticated user.
     * 
     * <p>Endpoint: POST /api/transactions</p>
     * 
     * <p>Request body example:</p>
     * <pre>
     * {
     *   "amount": 45.50,
     *   "description": "Lunch at Italian restaurant",
     *   "transactionDate": "2024-11-14",
     *   "type": "EXPENSE",
     *   "categoryId": 1,
     *   "paymentMethod": "Credit Card"
     * }
     * </pre>
     * 
     * @param request Transaction creation details
     * @return ResponseEntity with created transaction (201 Created)
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        TransactionResponse response = transactionService.createTransaction(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all transactions for the authenticated user.
     * 
     * <p>Endpoint: GET /api/transactions</p>
     * 
     * <p>Transactions are ordered by date (newest first).</p>
     * 
     * @return ResponseEntity with list of transactions (200 OK)
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getUserTransactions() {
        Long userId = securityUtils.getCurrentUserId();
        List<TransactionResponse> transactions = transactionService.getUserTransactions(userId);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Retrieves a specific transaction by ID.
     * 
     * <p>Endpoint: GET /api/transactions/{id}</p>
     * 
     * @param id Transaction ID
     * @return ResponseEntity with transaction details (200 OK)
     * @throws com.expenseinsight.api.exception.ResourceNotFoundException if transaction not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        TransactionResponse transaction = transactionService.getTransactionById(userId, id);
        return ResponseEntity.ok(transaction);
    }

    /**
     * Retrieves transactions filtered by type (EXPENSE or INCOME).
     * 
     * <p>Endpoint: GET /api/transactions/type/{type}</p>
     * 
     * <p>Example: GET /api/transactions/type/EXPENSE</p>
     * 
     * @param type Transaction type (EXPENSE or INCOME)
     * @return ResponseEntity with list of transactions (200 OK)
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByType(
            @PathVariable TransactionType type) {
        Long userId = securityUtils.getCurrentUserId();
        List<TransactionResponse> transactions = transactionService.getTransactionsByType(userId, type);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Retrieves transactions within a date range.
     * 
     * <p>Endpoint: GET /api/transactions/range?startDate={start}&endDate={end}</p>
     * 
     * <p>Example: GET /api/transactions/range?startDate=2024-01-01&endDate=2024-12-31</p>
     * 
     * @param startDate Start date (inclusive, format: yyyy-MM-dd)
     * @param endDate End date (inclusive, format: yyyy-MM-dd)
     * @return ResponseEntity with list of transactions (200 OK)
     */
    @GetMapping("/range")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = securityUtils.getCurrentUserId();
        List<TransactionResponse> transactions = transactionService.getTransactionsByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Updates an existing transaction.
     * 
     * <p>Endpoint: PUT /api/transactions/{id}</p>
     * 
     * <p>Request body example:</p>
     * <pre>
     * {
     *   "amount": 50.00,
     *   "description": "Updated lunch expense",
     *   "transactionDate": "2024-11-14",
     *   "type": "EXPENSE",
     *   "categoryId": 1,
     *   "paymentMethod": "Cash"
     * }
     * </pre>
     * 
     * @param id Transaction ID to update
     * @param request Updated transaction details
     * @return ResponseEntity with updated transaction (200 OK)
     * @throws com.expenseinsight.api.exception.ResourceNotFoundException if transaction not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTransactionRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        TransactionResponse response = transactionService.updateTransaction(userId, id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a transaction.
     * 
     * <p>Endpoint: DELETE /api/transactions/{id}</p>
     * 
     * @param id Transaction ID to delete
     * @return ResponseEntity with no content (204 No Content)
     * @throws com.expenseinsight.api.exception.ResourceNotFoundException if transaction not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        transactionService.deleteTransaction(userId, id);
        return ResponseEntity.noContent().build();
    }
}