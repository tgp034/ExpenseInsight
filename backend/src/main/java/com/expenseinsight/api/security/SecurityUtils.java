package com.expenseinsight.api.security;

import com.expenseinsight.api.entity.User;
import com.expenseinsight.api.exception.UnauthorizedException;
import com.expenseinsight.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Utility class for security-related operations.
 * 
 * <p>Provides helper methods to:</p>
 * <ul>
 *   <li>Get the currently authenticated user</li>
 *   <li>Get the email of the authenticated user</li>
 *   <li>Check if a user is authenticated</li>
 * </ul>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    /**
     * Gets the currently authenticated user from the security context.
     * 
     * @return Current authenticated user
     * @throws UnauthorizedException if no user is authenticated
     */
    public User getCurrentUser() {
        String email = getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    /**
     * Gets the email of the currently authenticated user.
     * 
     * @return Email of the authenticated user
     * @throws UnauthorizedException if no user is authenticated
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            String principalValue = principal.toString();
            if ("anonymousUser".equalsIgnoreCase(principalValue)) {
                throw new UnauthorizedException("No authenticated user found");
            }
            return principalValue;
        }
    }

    /**
     * Gets the ID of the currently authenticated user.
     * 
     * @return User ID
     * @throws UnauthorizedException if no user is authenticated
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}