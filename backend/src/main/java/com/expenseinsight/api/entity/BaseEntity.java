package com.expenseinsight.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Base entity class providing common audit fields for all entities.
 * This class is mapped as a @MappedSuperclass, meaning it won't be instantiated
 * directly but its fields will be inherited by child entities.
 * 
 * <p>Provides automatic timestamp management for entity creation and updates.</p>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    /**
     * Timestamp when the entity was created.
     * This field is set automatically on entity creation and cannot be updated.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

     /**
     * Timestamp when the entity was last updated.
     * This field is automatically updated whenever the entity is modified.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * JPA lifecycle callback executed before persisting a new entity.
     * Sets both createdAt and updatedAt to the current timestamp.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * JPA lifecycle callback executed before updating an existing entity.
     * Updates the updatedAt timestamp to the current time.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}