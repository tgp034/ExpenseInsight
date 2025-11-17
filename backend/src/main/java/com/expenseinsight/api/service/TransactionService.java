package com.expenseinsight.api.service;

import com.expenseinsight.api.dto.transaction.CreateTransactionRequest;
import com.expenseinsight.api.dto.transaction.TransactionResponse;
import com.expenseinsight.api.dto.transaction.UpdateTransactionRequest;
import com.expenseinsight.api.entity.Category;
import com.expenseinsight.api.entity.Transaction;
import com.expenseinsight.api.entity.User;
import com.expenseinsight.api.entity.enums.TransactionType;
import com.expenseinsight.api.exception.ResourceNotFoundException;
import com.expenseinsight.api.repository.CategoryRepository;
import com.expenseinsight.api.repository.TransactionRepository;
import com.expenseinsight.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing transactions.
 * 
 * <p>Provides business logic for:</p>
 * <ul>
 *   <li>Creating new transactions</li>
 *   <li>Retrieving user transactions with various filters</li>
 *   <li>Updating existing transactions</li>
 *   <li>Deleting transactions</li>
 *   <li>Calculating transaction summaries</li>
 * </ul>
 * 
 * <p>Security considerations:</p>
 * <ul>
 *   <li>All operations verify user ownership before allowing access</li>
 *   <li>Users can only access their own transactions</li>
 * </ul>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Creates a new transaction for a user.
     * 
     * <p>Process:</p>
     * <ol>
     *   <li>Validate user exists</li>
     *   <li>Validate category exists</li>
     *   <li>Create transaction entity</li>
     *   <li>Save to database</li>
     *   <li>Return transaction details</li>
     * </ol>
     * 
     * <p>Future enhancement: Call AI service to generate category suggestion
     * and comment based on description.</p>
     * 
     * @param userId ID of the user creating the transaction
     * @param request Transaction details
     * @return Created transaction details
     * @throws ResourceNotFoundException if user or category not found
     */
    @Transactional
    public TransactionResponse createTransaction(Long userId, CreateTransactionRequest request) {
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        // Create transaction entity
        Transaction transaction = Transaction.builder()
                .user(user)
                .category(category)
                .amount(request.getAmount())
                .description(request.getDescription())
                .transactionDate(request.getTransactionDate())
                .type(request.getType())
                .paymentMethod(request.getPaymentMethod())
                .build();

        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Convert to response DTO
        return mapToResponse(savedTransaction);
    }

    /**
     * Retrieves all transactions for a user.
     * 
     * @param userId ID of the user
     * @return List of user's transactions ordered by date (newest first)
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getUserTransactions(Long userId) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        // Get all user transactions
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);

        // Convert to response DTOs
        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific transaction by ID.
     * 
     * @param userId ID of the user
     * @param transactionId ID of the transaction
     * @return Transaction details
     * @throws ResourceNotFoundException if transaction not found or doesn't belong to user
     */
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));

        // Verify transaction belongs to user
        if (!transaction.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Transaction not found with id: " + transactionId);
        }

        return mapToResponse(transaction);
    }

    /**
     * Retrieves transactions filtered by type (EXPENSE or INCOME).
     * 
     * @param userId ID of the user
     * @param type Transaction type to filter by
     * @return List of transactions matching the type
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByType(Long userId, TransactionType type) {
        List<Transaction> transactions = transactionRepository.findByUserIdAndType(userId, type);
        
        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves transactions within a date range.
     * 
     * @param userId ID of the user
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of transactions within the date range
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository.findUserTransactionsByDateRange(userId, startDate, endDate);
        
        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing transaction.
     * 
     * @param userId ID of the user
     * @param transactionId ID of the transaction to update
     * @param request Updated transaction details
     * @return Updated transaction details
     * @throws ResourceNotFoundException if transaction not found or doesn't belong to user
     */
    @Transactional
    public TransactionResponse updateTransaction(Long userId, Long transactionId, UpdateTransactionRequest request) {
        // Find transaction
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));

        // Verify transaction belongs to user
        if (!transaction.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Transaction not found with id: " + transactionId);
        }

        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        // Update transaction fields
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setType(request.getType());
        transaction.setCategory(category);
        transaction.setPaymentMethod(request.getPaymentMethod());

        // Save updated transaction
        Transaction updatedTransaction = transactionRepository.save(transaction);

        return mapToResponse(updatedTransaction);
    }

    /**
     * Deletes a transaction.
     * 
     * @param userId ID of the user
     * @param transactionId ID of the transaction to delete
     * @throws ResourceNotFoundException if transaction not found or doesn't belong to user
     */
    @Transactional
    public void deleteTransaction(Long userId, Long transactionId) {
        // Find transaction
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));

        // Verify transaction belongs to user
        if (!transaction.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Transaction not found with id: " + transactionId);
        }

        // Delete transaction
        transactionRepository.delete(transaction);
    }

    /**
     * Maps a Transaction entity to a TransactionResponse DTO.
     * 
     * @param transaction Transaction entity
     * @return TransactionResponse DTO
     */
    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .transactionDate(transaction.getTransactionDate())
                .type(transaction.getType())
                .paymentMethod(transaction.getPaymentMethod())
                .categoryId(transaction.getCategory().getId())
                .categoryName(transaction.getCategory().getName())
                .categoryIcon(transaction.getCategory().getIcon())
                .categoryColor(transaction.getCategory().getColor())
                .aiComment(transaction.getAiComment())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}