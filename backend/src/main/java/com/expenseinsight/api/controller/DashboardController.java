package com.expenseinsight.api.controller;

import com.expenseinsight.api.dto.dashboard.DashboardSummary;
import com.expenseinsight.api.dto.dashboard.StatisticsResponse;
import com.expenseinsight.api.security.SecurityUtils;
import com.expenseinsight.api.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller for dashboard and statistics endpoints.
 * 
 * <p>Provides endpoints for:</p>
 * <ul>
 *   <li>Dashboard summary with key financial metrics</li>
 *   <li>Detailed statistics and spending analysis</li>
 * </ul>
 * 
 * <p>All endpoints require JWT authentication.</p>
 * 
 * <p>Base path: /api/dashboard</p>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final SecurityUtils securityUtils;

    /**
     * Retrieves comprehensive dashboard summary.
     * 
     * <p>Endpoint: GET /api/dashboard/summary</p>
     * 
     * <p>Query parameters:</p>
     * <ul>
     *   <li>startDate (optional): Start date (default: first day of current month)</li>
     *   <li>endDate (optional): End date (default: today)</li>
     * </ul>
     * 
     * <p>Example: GET /api/dashboard/summary?startDate=2024-11-01&endDate=2024-11-30</p>
     * 
     * @param startDate Start date of the period (optional)
     * @param endDate End date of the period (optional)
     * @return ResponseEntity with dashboard summary (200 OK)
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummary> getDashboardSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Long userId = securityUtils.getCurrentUserId();
        
        // Default to current month if dates not provided
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        DashboardSummary summary = dashboardService.getDashboardSummary(userId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    /**
     * Retrieves detailed statistics and spending analysis.
     * 
     * <p>Endpoint: GET /api/dashboard/statistics</p>
     * 
     * <p>Query parameters:</p>
     * <ul>
     *   <li>startDate (optional): Start date (default: first day of current month)</li>
     *   <li>endDate (optional): End date (default: today)</li>
     * </ul>
     * 
     * <p>Example: GET /api/dashboard/statistics?startDate=2024-01-01&endDate=2024-12-31</p>
     * 
     * @param startDate Start date of analysis period (optional)
     * @param endDate End date of analysis period (optional)
     * @return ResponseEntity with statistics (200 OK)
     */
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> getStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Long userId = securityUtils.getCurrentUserId();
        
        // Default to current month if dates not provided
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        StatisticsResponse statistics = dashboardService.getStatistics(userId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }
}