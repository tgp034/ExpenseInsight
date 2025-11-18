package com.expenseinsight.api.controller;

import com.expenseinsight.api.dto.user.ChangePasswordRequest;
import com.expenseinsight.api.dto.user.UpdateProfileRequest;
import com.expenseinsight.api.dto.user.UserProfileResponse;
import com.expenseinsight.api.security.SecurityUtils;
import com.expenseinsight.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user profile management.
 * 
 * <p>Provides endpoints for:</p>
 * <ul>
 *   <li>Viewing user profile</li>
 *   <li>Updating profile information</li>
 *   <li>Changing password</li>
 *   <li>Deactivating account</li>
 * </ul>
 * 
 * <p>All endpoints require JWT authentication.</p>
 * 
 * <p>Base path: /api/user</p>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SecurityUtils securityUtils;

    /**
     * Retrieves the authenticated user's profile.
     * 
     * <p>Endpoint: GET /api/user/profile</p>
     * 
     * <p>Returns complete profile information including account statistics.</p>
     * 
     * @return ResponseEntity with user profile (200 OK)
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        Long userId = securityUtils.getCurrentUserId();
        UserProfileResponse profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Updates the authenticated user's profile information.
     * 
     * <p>Endpoint: PUT /api/user/profile</p>
     * 
     * <p>Request body example:</p>
     * <pre>
     * {
     *   "firstName": "John",
     *   "lastName": "Smith"
     * }
     * </pre>
     * 
     * @param request Updated profile information
     * @return ResponseEntity with updated profile (200 OK)
     */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        UserProfileResponse profile = userService.updateProfile(userId, request);
        return ResponseEntity.ok(profile);
    }

    /**
     * Changes the authenticated user's password.
     * 
     * <p>Endpoint: PUT /api/user/password</p>
     * 
     * <p>Request body example:</p>
     * <pre>
     * {
     *   "currentPassword": "oldPassword123",
     *   "newPassword": "newSecurePassword456"
     * }
     * </pre>
     * 
     * <p>Requires current password verification for security.</p>
     * 
     * @param request Password change request
     * @return ResponseEntity with no content (204 No Content)
     */
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        userService.changePassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deactivates the authenticated user's account.
     * 
     * <p>Endpoint: DELETE /api/user/account</p>
     * 
     * <p>Performs a soft delete by setting the account to inactive.
     * All user data is preserved but the account cannot be used to login.</p>
     * 
     * @return ResponseEntity with no content (204 No Content)
     */
    @DeleteMapping("/account")
    public ResponseEntity<Void> deactivateAccount() {
        Long userId = securityUtils.getCurrentUserId();
        userService.deactivateAccount(userId);
        return ResponseEntity.noContent().build();
    }
}