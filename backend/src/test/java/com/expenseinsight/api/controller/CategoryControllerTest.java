package com.expenseinsight.api.controller;

import com.expenseinsight.api.dto.category.CategoryResponse;
import com.expenseinsight.api.entity.enums.TransactionType;
import com.expenseinsight.api.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    @DisplayName("should return all categories")
    void getAllCategories_returnsOkResponse() throws Exception {
        CategoryResponse groceries = CategoryResponse.builder()
                .id(1L)
                .name("Groceries")
                .type(TransactionType.EXPENSE)
                .icon("shopping_cart")
                .color("#00FF00")
                .build();

        CategoryResponse salary = CategoryResponse.builder()
                .id(2L)
                .name("Salary")
                .type(TransactionType.INCOME)
                .icon("payments")
                .color("#0000FF")
                .build();

        when(categoryService.getAllCategories()).thenReturn(List.of(groceries, salary));

        mockMvc.perform(get("/api/categories")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Groceries"))
                .andExpect(jsonPath("$[1].name").value("Salary"));
    }

    @Test
    @DisplayName("should filter categories by transaction type")
    void getCategoriesByType_returnsFilteredCategories() throws Exception {
        CategoryResponse groceries = CategoryResponse.builder()
                .id(1L)
                .name("Groceries")
                .type(TransactionType.EXPENSE)
                .icon("shopping_cart")
                .color("#00FF00")
                .build();

        when(categoryService.getCategoriesByType(TransactionType.EXPENSE)).thenReturn(List.of(groceries));

        mockMvc.perform(get("/api/categories/type/EXPENSE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("EXPENSE"))
                .andExpect(jsonPath("$[0].name").value("Groceries"));
    }
}