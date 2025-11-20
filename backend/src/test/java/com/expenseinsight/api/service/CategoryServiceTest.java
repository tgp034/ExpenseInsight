package com.expenseinsight.api.service;

import com.expenseinsight.api.dto.category.CategoryResponse;
import com.expenseinsight.api.entity.Category;
import com.expenseinsight.api.entity.enums.TransactionType;
import com.expenseinsight.api.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAllCategories_mapsEntitiesToResponses() {
        Category groceries = Category.builder()
                .id(1L)
                .name("Groceries")
                .type(TransactionType.EXPENSE)
                .icon("shopping_cart")
                .color("#00FF00")
                .build();

        Category salary = Category.builder()
                .id(2L)
                .name("Salary")
                .type(TransactionType.INCOME)
                .icon("payments")
                .color("#0000FF")
                .build();

        when(categoryRepository.findAll()).thenReturn(List.of(groceries, salary));

        List<CategoryResponse> responses = categoryService.getAllCategories();

        assertThat(responses)
                .hasSize(2)
                .extracting(CategoryResponse::getName)
                .containsExactlyInAnyOrder("Groceries", "Salary");
        assertThat(responses)
                .extracting(CategoryResponse::getType)
                .containsExactlyInAnyOrder(TransactionType.EXPENSE, TransactionType.INCOME);
    }

    @Test
    void getCategoriesByType_filtersByTransactionType() {
        Category groceries = Category.builder()
                .id(1L)
                .name("Groceries")
                .type(TransactionType.EXPENSE)
                .icon("shopping_cart")
                .color("#00FF00")
                .build();

        when(categoryRepository.findByType(TransactionType.EXPENSE)).thenReturn(List.of(groceries));

        List<CategoryResponse> responses = categoryService.getCategoriesByType(TransactionType.EXPENSE);

        assertThat(responses)
                .singleElement()
                .satisfies(response -> {
                    assertThat(response.getId()).isEqualTo(1L);
                    assertThat(response.getName()).isEqualTo("Groceries");
                    assertThat(response.getType()).isEqualTo(TransactionType.EXPENSE);
                });
    }
}