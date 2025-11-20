package com.expenseinsight.api.service;

import com.expenseinsight.api.dto.ai.CategorySuggestionRequest;
import com.expenseinsight.api.dto.ai.CategorySuggestionResponse;
import com.expenseinsight.api.dto.ai.WeeklySummaryResponse;
import com.expenseinsight.api.entity.Category;
import com.expenseinsight.api.entity.Transaction;
import com.expenseinsight.api.entity.enums.TransactionType;
import com.expenseinsight.api.repository.CategoryRepository;
import com.expenseinsight.api.repository.TransactionRepository;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.springframework.beans.factory.ObjectProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;

@Service
@Slf4j
public class OpenAiSdkService {

    private final OpenAIClient openAIClient;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    @Value("${openai.model:gpt-4o-mini}")
    private String defaultModel;

    @Value("${openai.max.tokens:150}")
    private Integer defaultMaxTokens;

    @Value("${openai.temperature:0.7}")
    private Double defaultTemperature;

    @Value("${openai.retry.count:3}")
    private int retryCount;

    @Value("${openai.retry.initialDelayMs:500}")
    private long retryInitialDelayMs;

    // Resilience4j components
    private io.github.resilience4j.retry.Retry resilienceRetry;
    private io.github.resilience4j.circuitbreaker.CircuitBreaker resilienceCircuitBreaker;

        public OpenAiSdkService(ObjectProvider<OpenAIClient> openAIClientProvider,
                                                        CategoryRepository categoryRepository,
                                                        TransactionRepository transactionRepository) {
                this.openAIClient = openAIClientProvider.getIfAvailable(() -> null);
                this.categoryRepository = categoryRepository;
                this.transactionRepository = transactionRepository;
        }

        @jakarta.annotation.PostConstruct
        private void initResilience() {
        io.github.resilience4j.retry.RetryConfig retryConfig = io.github.resilience4j.retry.RetryConfig.custom()
                .maxAttempts(retryCount + 1) // resilience4j counts attempts including initial
                .waitDuration(java.time.Duration.ofMillis(retryInitialDelayMs))
                .build();

        resilienceRetry = io.github.resilience4j.retry.Retry.of("openaiRetry", retryConfig);

        io.github.resilience4j.circuitbreaker.CircuitBreakerConfig cbConfig = io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(java.time.Duration.ofSeconds(30))
                .slidingWindowSize(10)
                .build();

        resilienceCircuitBreaker = io.github.resilience4j.circuitbreaker.CircuitBreaker.of("openaiCB", cbConfig);
    }

    public CategorySuggestionResponse suggestCategory(CategorySuggestionRequest request) {
        List<Category> expenseCategories = categoryRepository.findByType(TransactionType.EXPENSE);

        String categoriesText = expenseCategories.stream()
                .map(c -> String.format("- %s (%s)", c.getName(), c.getIcon()))
                .collect(Collectors.joining("\n"));

        String prompt = String.format(
                "You are a financial assistant. Based on the following transaction details, " +
                        "suggest the most appropriate category from the list below.\n\n" +
                        "Transaction description: %s\n" +
                        "Amount: $%.2f\n\n" +
                        "Available categories:\n%s\n\n" +
                        "Respond ONLY with the exact category name from the list, followed by a pipe (|), " +
                        "then a confidence score (0-1), followed by another pipe (|), " +
                        "then a brief comment (max 50 words).\n" +
                        "Format: CategoryName|0.95|Your comment here",
                request.getDescription(),
                request.getAmount(),
                categoriesText);

        String raw = callChatCompletion(defaultModel, prompt, defaultMaxTokens, defaultTemperature);
        if (raw == null || raw.isBlank()) {
            return getDefaultSuggestion(expenseCategories);
        }

        String[] parts = raw.split("\\|");
        if (parts.length >= 2) {
            String categoryName = parts[0].trim();
            Double confidence;
            try {
                confidence = Double.parseDouble(parts[1].trim());
            } catch (Exception e) {
                confidence = 0.5;
            }
            String comment = parts.length > 2 ? parts[2].trim() : "";

            Category matchedCategory = expenseCategories.stream()
                    .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                    .findFirst()
                    .orElse(expenseCategories.stream()
                            .filter(c -> c.getName().contains("Other"))
                            .findFirst()
                            .orElse(expenseCategories.get(0)));

            return CategorySuggestionResponse.builder()
                    .suggestedCategoryId(matchedCategory.getId())
                    .suggestedCategoryName(matchedCategory.getName())
                    .confidence(confidence)
                    .comment(comment)
                    .build();
        }

        return getDefaultSuggestion(expenseCategories);
    }

    public WeeklySummaryResponse generateWeeklySummary(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        List<Transaction> transactions = transactionRepository.findUserTransactionsByDateRange(userId, startDate,
                endDate);

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        List<String> topCategories = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)))
                .entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(3)
                .map(e -> String.format("%s: $%.2f", e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        String prompt = String.format(
                "You are a personal finance advisor. Generate a brief weekly financial summary " +
                        "and 2-3 actionable recommendations based on the following data:\n\n" +
                        "Week: %s to %s\n" +
                        "Total Income: $%.2f\n" +
                        "Total Expenses: $%.2f\n" +
                        "Net Balance: $%.2f\n" +
                        "Top Spending Categories:\n%s\n\n" +
                        "Provide:\n" +
                        "1. A brief summary (max 100 words)\n" +
                        "2. 2-3 specific recommendations (separate with '||')\n\n" +
                        "Format: Summary text||Recommendation 1||Recommendation 2",
                startDate.format(DateTimeFormatter.ISO_DATE),
                endDate.format(DateTimeFormatter.ISO_DATE),
                totalIncome,
                totalExpenses,
                netBalance,
                String.join("\n", topCategories));

        String raw = callChatCompletion(defaultModel, prompt, 300, defaultTemperature);
        if (raw == null || raw.isBlank()) {
            return getBasicWeeklySummary(userId);
        }

        String[] parts = raw.split("\\|\\|");
        String aiSummary = parts.length > 0 ? parts[0].trim() : "Weekly summary generated.";
        List<String> recommendations = parts.length > 1
                ? Arrays.stream(parts).skip(1).map(String::trim).collect(Collectors.toList())
                : List.of("Continue tracking your expenses regularly.");

        return WeeklySummaryResponse.builder()
                .weekStart(startDate.format(DateTimeFormatter.ISO_DATE))
                .weekEnd(endDate.format(DateTimeFormatter.ISO_DATE))
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .topCategories(topCategories)
                .aiSummary(aiSummary)
                .recommendations(recommendations)
                .build();
    }

    private String callChatCompletion(String model, String prompt, Integer maxTokens, Double temperature) {
                if (openAIClient == null) {
                        log.warn("OpenAI client not configured (openai.enabled=false). Skipping AI call.");
                        return null;
                }
        int tokensEstimate = Math.max(1, prompt.length() / 4);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = (auth != null && auth.isAuthenticated() && auth.getName() != null) ? auth.getName() : "anonymous";

        log.info("OpenAI request => user={}, model={}, tokensEstimate={}", userId, model, tokensEstimate);

        Supplier<String> supplier = () -> {
            try {
                ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                        .addUserMessage(prompt)
                        .model(model)
                        .maxCompletionTokens(maxTokens)
                        .temperature(temperature)
                        .build();

                ChatCompletion completion = openAIClient.chat().completions().create(params);

                String result = completion.choices().stream()
                        .flatMap(choice -> choice.message().content().stream())
                        .collect(Collectors.joining("\n"));

                log.info("OpenAI response success => user={}, model={}, tokensEstimate={}", userId, model, tokensEstimate);
                return result;
            } catch (Exception e) {
                log.error("OpenAI request failed => user={}, model={}, tokensEstimate={}", userId, model, tokensEstimate);
                throw new RuntimeException(e);
            }
        };

        Supplier<String> decorated = io.github.resilience4j.retry.Retry.decorateSupplier(resilienceRetry, supplier);
        decorated = io.github.resilience4j.circuitbreaker.CircuitBreaker.decorateSupplier(resilienceCircuitBreaker, decorated);

        try {
            return decorated.get();
        } catch (Exception e) {
            log.error("Error calling OpenAI SDK (decorated) => user={}", userId, e);
            return null;
        }
    }

    private CategorySuggestionResponse getDefaultSuggestion(List<Category> categories) {
        Category defaultCategory = categories.stream()
                .filter(c -> c.getName().contains("Other"))
                .findFirst()
                .orElse(categories.get(0));

        return CategorySuggestionResponse.builder()
                .suggestedCategoryId(defaultCategory.getId())
                .suggestedCategoryName(defaultCategory.getName())
                .confidence(0.5)
                .comment("Unable to auto-categorize. Please select manually.")
                .build();
    }

    private WeeklySummaryResponse getBasicWeeklySummary(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        List<Transaction> transactions = transactionRepository.findUserTransactionsByDateRange(userId, startDate,
                endDate);

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return WeeklySummaryResponse.builder()
                .weekStart(startDate.format(DateTimeFormatter.ISO_DATE))
                .weekEnd(endDate.format(DateTimeFormatter.ISO_DATE))
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(totalIncome.subtract(totalExpenses))
                .topCategories(List.of())
                .aiSummary("Weekly summary generated. AI insights temporarily unavailable.")
                .recommendations(List.of("Keep tracking your expenses", "Review your budgets regularly"))
                .build();
    }

}
