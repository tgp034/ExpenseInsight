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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void createTransaction_persistsAndReturnsResponse() {
        User user = User.builder().id(1L).email("test@example.com").build();
        Category category = Category.builder().id(2L).name("Food").icon("icon").color("#fff").build();

        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .amount(new BigDecimal("25.50"))
                .description("Lunch")
                .transactionDate(LocalDate.of(2024, 1, 1))
                .type(TransactionType.EXPENSE)
                .categoryId(category.getId())
                .paymentMethod("Card")
                .build();

        Transaction savedTransaction = Transaction.builder()
                .id(10L)
                .user(user)
                .category(category)
                .amount(request.getAmount())
                .description(request.getDescription())
                .transactionDate(request.getTransactionDate())
                .type(request.getType())
                .paymentMethod(request.getPaymentMethod())
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionResponse response = transactionService.createTransaction(user.getId(), request);

        assertEquals(savedTransaction.getId(), response.getId());
        assertEquals(category.getName(), response.getCategoryName());
        assertEquals(request.getAmount(), response.getAmount());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void getUserTransactions_throwsWhenUserMissing() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> transactionService.getUserTransactions(99L));
        verify(transactionRepository, never()).findByUserIdOrderByTransactionDateDesc(anyLong());
    }

    @Test
    void updateTransaction_validatesOwnershipAndUpdates() {
        User user = User.builder().id(1L).build();
        Category initialCategory = Category.builder().id(5L).build();
        Category newCategory = Category.builder().id(6L).build();

        Transaction existing = Transaction.builder()
                .id(20L)
                .user(user)
                .category(initialCategory)
                .amount(new BigDecimal("10.00"))
                .description("Coffee")
                .transactionDate(LocalDate.of(2024, 2, 1))
                .type(TransactionType.EXPENSE)
                .paymentMethod("Cash")
                .build();

        UpdateTransactionRequest request = UpdateTransactionRequest.builder()
                .amount(new BigDecimal("12.00"))
                .description("Coffee and snack")
                .transactionDate(LocalDate.of(2024, 2, 2))
                .type(TransactionType.EXPENSE)
                .categoryId(newCategory.getId())
                .paymentMethod("Card")
                .build();

        when(transactionRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(newCategory.getId())).thenReturn(Optional.of(newCategory));
        when(transactionRepository.save(existing)).thenReturn(existing);

        TransactionResponse response = transactionService.updateTransaction(user.getId(), existing.getId(), request);

        assertEquals(request.getAmount(), response.getAmount());
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(newCategory.getId(), response.getCategoryId());
    }

    @Test
    void updateTransaction_throwsWhenUserDoesNotOwnTransaction() {
        User owner = User.builder().id(1L).build();
        User requester = User.builder().id(2L).build();
        Transaction transaction = Transaction.builder().id(30L).user(owner).build();

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        assertThrows(ResourceNotFoundException.class, () -> transactionService.updateTransaction(requester.getId(), transaction.getId(),
                UpdateTransactionRequest.builder().categoryId(1L).build()));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void getTransactionById_returnsResponseWhenOwnerMatches() {
        User user = User.builder().id(1L).build();
        Category category = Category.builder().id(2L).name("Utilities").icon("icon").color("#000").build();
        Transaction transaction = Transaction.builder()
                .id(3L)
                .user(user)
                .category(category)
                .amount(new BigDecimal("100"))
                .description("Electricity")
                .transactionDate(LocalDate.of(2024, 3, 1))
                .type(TransactionType.EXPENSE)
                .paymentMethod("Transfer")
                .build();

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        TransactionResponse response = transactionService.getTransactionById(user.getId(), transaction.getId());

        assertEquals(transaction.getDescription(), response.getDescription());
        assertEquals(category.getName(), response.getCategoryName());
    }
}