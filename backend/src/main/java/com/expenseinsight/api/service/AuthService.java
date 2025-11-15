package com.expenseinsight.api.service;

import com.expenseinsight.api.dto.auth.AuthResponse;
import com.expenseinsight.api.dto.auth.LoginRequest;
import com.expenseinsight.api.dto.auth.RegisterRequest;
import com.expenseinsight.api.entity.User;
import com.expenseinsight.api.exception.DuplicateResourceException;
import com.expenseinsight.api.exception.ResourceNotFoundException;
import com.expenseinsight.api.repository.UserRepository;
import com.expenseinsight.api.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class handling authentication operations.
 * 
 * <p>Provides business logic for:</p>
 * <ul>
 *   <li>User registration with password encryption</li>
 *   <li>User login with JWT token generation</li>
 *   <li>Email uniqueness validation</li>
 * </ul>
 * 
 * <p>Security features:</p>
 * <ul>
 *   <li>Passwords are encrypted using BCrypt before storage</li>
 *   <li>JWT tokens are generated upon successful authentication</li>
 *   <li>Users are automatically set as active upon registration</li>
 * </ul>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user in the system.
     * 
     * <p>Registration process:</p>
     * <ol>
     *   <li>Validate email is not already registered</li>
     *   <li>Encrypt the password using BCrypt</li>
     *   <li>Create and save user entity</li>
     *   <li>Generate JWT token for immediate login</li>
     *   <li>Return authentication response with token and user details</li>
     * </ol>
     * 
     * @param request Registration request containing user details
     * @return AuthResponse with JWT token and user information
     * @throws DuplicateResourceException if email is already registered
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                "Email already registered: " + request.getEmail()
            );
        }

        // Create new user entity
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .isActive(true)
                .build();

        // Save user to database
        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getEmail());

        // Build and return authentication response
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .build();
    }

    /**
     * Authenticates a user and generates a JWT token.
     * 
     * <p>Login process:</p>
     * <ol>
     *   <li>Authenticate credentials using Spring Security</li>
     *   <li>Retrieve user details from database</li>
     *   <li>Generate JWT token</li>
     *   <li>Return authentication response with token and user details</li>
     * </ol>
     * 
     * @param request Login request containing email and password
     * @return AuthResponse with JWT token and user information
     * @throws ResourceNotFoundException if user not found after successful authentication
     */
    public AuthResponse login(LoginRequest request) {
        // Authenticate user credentials (throws BadCredentialsException if invalid)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Retrieve user from database
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "User not found with email: " + request.getEmail()
                ));

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        // Build and return authentication response
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}