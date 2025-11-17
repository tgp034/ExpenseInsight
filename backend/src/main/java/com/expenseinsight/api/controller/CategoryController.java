package com.expenseinsight.api.controller;

import com.expenseinsight.api.dto.category.CategoryResponse;
import com.expenseinsight.api.entity.enums.TransactionType;
import com.expenseinsight.api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for category operations.
 * 
 * <p>Provides endpoints for retrieving available categories
 * for transaction classification.</p>
 * 
 * <p>All endpoints require JWT authentication.</p>
 * 
 * <p>Base path: /api/categories</p>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Retrieves all available categories.
     * 
     * <p>Endpoint: GET /api/categories</p>
     * 
     * @return ResponseEntity with list of categories (200 OK)
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Retrieves categories filtered by type.
     * 
     * <p>Endpoint: GET /api/categories/type/{type}</p>
     * 
     * <p>Example: GET /api/categories/type/EXPENSE</p>
     * 
     * @param type Transaction type (EXPENSE or INCOME)
     * @return ResponseEntity with list of categories (200 OK)
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<CategoryResponse>> getCategoriesByType(@PathVariable TransactionType type) {
        List<CategoryResponse> categories = categoryService.getCategoriesByType(type);
        return ResponseEntity.ok(categories);
    }
}