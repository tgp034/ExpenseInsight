package com.expenseinsight.api.controller;

import com.expenseinsight.api.dto.auth.AuthResponse;
import com.expenseinsight.api.dto.auth.LoginRequest;
import com.expenseinsight.api.dto.auth.RegisterRequest;
import com.expenseinsight.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 * 
 * <p>Provides public endpoints for:</p>
 * <ul>
 *   <li>User registration</li>
 *   <li>User login</li>
 * </ul>
 * 
 * <p>All endpoints in this controller are public (no authentication required)
 * as they are used to obtain authentication credentials.</p>
 * 
 * <p>Base path: /api/auth</p>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user account.
     * 
     * <p>Creates a new user with encrypted password and returns a JWT token
     * for immediate authentication.</p>
     * 
     * <p>Endpoint: POST /api/auth/register</p>
     * 
     * <p>Request body example:</p>
     * <pre>
     * {
     *   "email": "john.doe@example.com",
     *   "password": "securePassword123",
     *   "firstName": "John",
     *   "lastName": "Doe"
     * }
     * </pre>
     * 
     * <p>Response example:</p>
     * <pre>
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "type": "Bearer",
     *   "userId": 1,
     *   "email": "john.doe@example.com",
     *   "firstName": "John",
     *   "lastName": "Doe"
     * }
     * </pre>
     * 
     * @param request Registration request with user details
     * @return ResponseEntity with AuthResponse (201 Created)
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticates a user and returns a JWT token.
     * 
     * <p>Validates credentials and generates a JWT token for authenticated access.</p>
     * 
     * <p>Endpoint: POST /api/auth/login</p>
     * 
     * <p>Request body example:</p>
     * <pre>
     * {
     *   "email": "john.doe@example.com",
     *   "password": "securePassword123"
     * }
     * </pre>
     * 
     * <p>Response example:</p>
     * <pre>
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "type": "Bearer",
     *   "userId": 1,
     *   "email": "john.doe@example.com",
     *   "firstName": "John",
     *   "lastName": "Doe"
     * }
     * </pre>
     * 
     * @param request Login request with email and password
     * @return ResponseEntity with AuthResponse (200 OK)
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}