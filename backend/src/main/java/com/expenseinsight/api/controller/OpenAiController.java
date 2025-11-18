package com.expenseinsight.api.controller;

import com.expenseinsight.api.dto.ai.CategorySuggestionRequest;
import com.expenseinsight.api.dto.ai.CategorySuggestionResponse;
import com.expenseinsight.api.dto.ai.WeeklySummaryResponse;
import com.expenseinsight.api.security.SecurityUtils;
import com.expenseinsight.api.service.OpenAiSdkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that exposes AI endpoints backed by the official SDK.
 * Endpoints return 503 while the SDK is not configured or in case of errors.
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class OpenAiController {

    private final OpenAiSdkService openAiSdkService;
    private final SecurityUtils securityUtils;

    @PostMapping("/suggest-category")
    public ResponseEntity<CategorySuggestionResponse> suggestCategory(@Valid @RequestBody CategorySuggestionRequest request) {
        try {
            CategorySuggestionResponse response = openAiSdkService.suggestCategory(request);
            return ResponseEntity.ok(response);
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(503).build();
        }
    }

    @GetMapping("/weekly-summary")
    public ResponseEntity<WeeklySummaryResponse> weeklySummary() {
        try {
            Long userId = securityUtils.getCurrentUserId();
            WeeklySummaryResponse response = openAiSdkService.generateWeeklySummary(userId);
            return ResponseEntity.ok(response);
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(503).build();
        }
    }

}
