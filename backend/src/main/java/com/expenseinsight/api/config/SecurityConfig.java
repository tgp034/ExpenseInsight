package com.expenseinsight.api.config;

import com.expenseinsight.api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Security configuration class for ExpenseInsight API.
 * 
 * <p>
 * This configuration class sets up:
 * </p>
 * <ul>
 * <li>JWT-based authentication (stateless)</li>
 * <li>Password encryption using BCrypt</li>
 * <li>CORS configuration for frontend communication</li>
 * <li>Public and protected endpoints</li>
 * <li>Custom authentication filter chain</li>
 * </ul>
 * 
 * <p>
 * Security strategy:
 * </p>
 * <ul>
 * <li>Stateless sessions (no server-side session storage)</li>
 * <li>JWT tokens for authentication</li>
 * <li>BCrypt for password hashing (strength 10)</li>
 * <li>CSRF disabled (not needed for stateless JWT authentication)</li>
 * </ul>
 * 
 * <p>
 * Public endpoints (no authentication required):
 * </p>
 * <ul>
 * <li>POST /api/auth/register - User registration</li>
 * <li>POST /api/auth/login - User login</li>
 * </ul>
 * 
 * <p>
 * All other endpoints require valid JWT authentication.
 * </p>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Value("#{'${cors.allowed.origins:http://localhost:3000}'.split(',')}")
    private List<String> allowedOrigins;

    /**
     * Configures the security filter chain.
     * 
     * <p>
     * Security rules:
     * </p>
     * <ul>
     * <li>Disable CSRF (not needed for stateless API)</li>
     * <li>Enable CORS with configured origins</li>
     * <li>Allow public access to auth endpoints</li>
     * <li>Require authentication for all other endpoints</li>
     * <li>Use stateless session management</li>
     * <li>Add JWT filter before username/password authentication filter</li>
     * </ul>
     * 
     * @param http HttpSecurity object to configure
     * @return Configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (not needed for stateless JWT authentication)
                .csrf(AbstractHttpConfigurer::disable)

                // Configure CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers("/api/auth/**").permitAll()

                        // All other endpoints require authentication
                        .anyRequest().authenticated())

                // Configure session management (stateless)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Set authentication provider
                .authenticationProvider(authenticationProvider(userDetailsService, passwordEncoder()))

                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings.
     * 
     * <p>
     * Allows the frontend application to communicate with the API.
     * Configuration supports:
     * </p>
     * <ul>
     * <li>Allowed origins: localhost:3000 (Next.js frontend)</li>
     * <li>Allowed methods: GET, POST, PUT, DELETE, PATCH</li>
     * <li>Allowed headers: All headers</li>
     * <li>Credentials: Allowed (for cookies/auth headers)</li>
     * </ul>
     * 
     * @return CorsConfigurationSource with configured CORS rules
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow configured frontend origins (comma-separated)
        List<String> origins = allowedOrigins.stream()
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .collect(Collectors.toList());

        configuration.setAllowedOriginPatterns(origins);

        // Allow common HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));

        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Apply configuration to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * Configures the authentication provider.
     * 
     * <p>
     * Uses DaoAuthenticationProvider with:
     * </p>
     * <ul>
     * <li>Custom UserDetailsService to load users from database</li>
     * <li>BCrypt password encoder for password verification</li>
     * </ul>
     * 
     * @return Configured AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        // optionally:
        // provider.setUserDetailsPasswordService(userDetailsPasswordService);
        // provider.setCompromisedPasswordChecker(compromisedPasswordChecker);
        return provider;
    }

    /**
     * Provides the authentication manager bean.
     * 
     * <p>
     * Required for manual authentication (e.g., in login endpoint).
     * </p>
     * 
     * @param config AuthenticationConfiguration
     * @return AuthenticationManager instance
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configures the password encoder.
     * 
     * <p>
     * Uses BCrypt hashing algorithm with default strength (10 rounds).
     * BCrypt is a secure, adaptive hashing function designed for password storage.
     * </p>
     * 
     * <p>
     * Benefits of BCrypt:
     * </p>
     * <ul>
     * <li>Resistant to brute-force attacks</li>
     * <li>Built-in salt generation</li>
     * <li>Adaptive (can increase cost factor over time)</li>
     * </ul>
     * 
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}