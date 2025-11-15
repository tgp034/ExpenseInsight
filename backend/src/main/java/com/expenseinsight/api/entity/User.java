package com.expenseinsight.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a user in the ExpenseInsight application.
 * 
 * <p>A user can have multiple transactions and budgets. The user entity includes
 * basic authentication information (email and password) along with profile details.</p>
 * 
 * <p>Security considerations:</p>
 * <ul>
 *   <li>Passwords should always be encrypted before storage (BCrypt recommended)</li>
 *   <li>Email is unique across the system and used as the primary identifier for authentication</li>
 *   <li>The isActive flag allows soft deletion of users</li>
 * </ul>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    /**
     * Unique identifier for the user.
     * Auto-generated using database sequence.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User's email address.
     * Must be unique across the system and is used as the login identifier.
     * Must follow valid email format.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * User's encrypted password.
     * Must be at least 8 characters long (validated before encryption).
     * Should be encrypted using BCrypt before storage.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Column(nullable = false)
    private String password;

    /**
     * User's first name.
     * Maximum length of 100 characters.
     */
    @NotBlank(message = "First name is required")
    @Size(max = 100)
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    /**
     * User's last name.
     * Maximum length of 100 characters.
     */
    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /**
     * Flag indicating whether the user account is active.
     * Defaults to true. Can be set to false for soft deletion.
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * List of all transactions associated with this user.
     * Cascade operations ensure transactions are deleted when user is deleted.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    /**
     * List of all budgets defined by this user.
     * Cascade operations ensure budgets are deleted when user is deleted.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Budget> budgets = new ArrayList<>();

    /**
     * Returns the user's full name by concatenating first and last name.
     * 
     * @return Full name in format "FirstName LastName"
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
} 