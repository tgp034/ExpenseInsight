package com.expenseinsight.api.security;

import com.expenseinsight.api.entity.User;
import com.expenseinsight.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Custom UserDetailsService implementation for Spring Security authentication.
 * 
 * <p>This service is responsible for loading user-specific data during the
 * authentication process. It retrieves user information from the database
 * and converts it into a Spring Security UserDetails object.</p>
 * 
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Load user by email (username in Spring Security context)</li>
 *   <li>Verify user is active before allowing authentication</li>
 *   <li>Convert application User entity to Spring Security UserDetails</li>
 * </ul>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads user details by email address.
     * 
     * <p>This method is called by Spring Security during authentication.
     * It retrieves the user from the database and converts it to a
     * UserDetails object that Spring Security can work with.</p>
     * 
     * <p>Only active users can be authenticated. Inactive users will
     * receive an authentication error.</p>
     * 
     * @param email Email address of the user (used as username)
     * @return UserDetails object containing user information and authorities
     * @throws UsernameNotFoundException if user is not found or is inactive
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found or inactive with email: " + email));

        // Currently, we're not implementing roles/authorities
        // In future versions, this can be extended to include user roles
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>() // Empty authorities list for now
        );
    }
}