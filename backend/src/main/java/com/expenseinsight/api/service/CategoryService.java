package com.expenseinsight.api.service;

import com.expenseinsight.api.dto.category.CategoryResponse;
import com.expenseinsight.api.entity.Category;
import com.expenseinsight.api.entity.enums.TransactionType;
import com.expenseinsight.api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing categories.
 * 
 * <p>Provides business logic for retrieving categories.</p>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Retrieves all categories.
     * 
     * @return List of all categories
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves categories filtered by type.
     * 
     * @param type Transaction type (EXPENSE or INCOME)
     * @return List of categories matching the type
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByType(TransactionType type) {
        List<Category> categories = categoryRepository.findByType(type);
        return categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Maps a Category entity to a CategoryResponse DTO.
     * 
     * @param category Category entity
     * @return CategoryResponse DTO
     */
    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .icon(category.getIcon())
                .color(category.getColor())
                .build();
    }
}