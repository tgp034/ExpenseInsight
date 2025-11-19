package com.expenseinsight.api.service;

import com.expenseinsight.api.dto.user.ChangePasswordRequest;
import com.expenseinsight.api.dto.user.UpdateProfileRequest;
import com.expenseinsight.api.dto.user.UserProfileResponse;
import com.expenseinsight.api.entity.User;
import com.expenseinsight.api.exception.ResourceNotFoundException;
import com.expenseinsight.api.exception.UnauthorizedException;
import com.expenseinsight.api.repository.BudgetRepository;
import com.expenseinsight.api.repository.TransactionRepository;
import com.expenseinsight.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for user profile management.
 * 
 * <p>Provides business logic for:</p>
 * <ul>
 *   <li>Retrieving user profile information</li>
 *   <li>Updating profile details (name)</li>
 *   <li>Changing password with verification</li>
 *   <li>Account statistics (transaction count, budget count)</li>
 * </ul>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Retrieves complete profile information for a user.
     * 
     * <p>Includes:</p>
     * <ul>
     *   <li>Basic profile data (name, email)</li>
     *   <li>Account metadata (created date, active status)</li>
     *   <li>Usage statistics (transaction count, budget count)</li>
     * </ul>
     * 
     * @param userId ID of the user
     * @return User profile with statistics
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Count user's transactions and budgets
        Integer totalTransactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId).size();
        Integer totalBudgets = budgetRepository.findByUserId(userId).size();

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .totalTransactions(totalTransactions)
                .totalBudgets(totalBudgets)
                .build();
    }

    /**
     * Updates user profile information (first and last name).
     * 
     * <p>Email cannot be changed as it's used for authentication.</p>
     * 
     * @param userId ID of the user
     * @param request Updated profile information
     * @return Updated user profile
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Update profile fields
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User updatedUser = userRepository.save(user);

        // Recalculate statistics
        Integer totalTransactions = Math.toIntExact(transactionRepository.countByUserId(userId));
        Integer totalBudgets = Math.toIntExact(budgetRepository.countByUserId(userId));
        
        return UserProfileResponse.builder()
                .id(updatedUser.getId())
                .email(updatedUser.getEmail())
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .fullName(updatedUser.getFullName())
                .isActive(updatedUser.getIsActive())
                .createdAt(updatedUser.getCreatedAt())
                .totalTransactions(totalTransactions)
                .totalBudgets(totalBudgets)
                .build();
    }

    /**
     * Changes user password after verifying current password.
     * 
     * <p>Security measures:</p>
     * <ul>
     *   <li>Requires current password verification</li>
     *   <li>New password is encrypted before storage</li>
     *   <li>Validates new password meets minimum requirements</li>
     * </ul>
     * 
     * @param userId ID of the user
     * @param request Password change request with current and new passwords
     * @throws ResourceNotFoundException if user not found
     * @throws UnauthorizedException if current password is incorrect
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        // Encrypt and set new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Deactivates a user account (soft delete).
     * 
     * <p>Sets the user's isActive flag to false, preventing login
     * while preserving all user data.</p>
     * 
     * @param userId ID of the user to deactivate
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional
    public void deactivateAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setIsActive(false);
        userRepository.save(user);
    }
}