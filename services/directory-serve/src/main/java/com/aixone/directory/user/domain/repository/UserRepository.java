package com.aixone.directory.user.domain.repository;

import java.util.Optional;

import com.aixone.directory.user.domain.aggregate.User;

/**
 * Repository interface for the User aggregate.
 * Defines the contract for persistence operations.
 */
public interface UserRepository {

    /**
     * Saves a user aggregate.
     * @param user The user to save.
     */
    void save(User user);

    /**
     * Finds a user by their ID.
     * @param id The UUID of the user.
     * @return An Optional containing the user if found, or empty otherwise.
     */
    Optional<User> findById(String id);

    /**
     * Finds a user by their tenant ID and email address.
     * @param tenantId The tenant ID of the user.
     * @param email The email address of the user.
     * @return An Optional containing the user if found, or empty otherwise.
     */
    Optional<User> findByTenantIdAndEmail(String tenantId, String email);

    /**
     * Finds a user by their tenant ID and ID.
     * @param tenantId The tenant ID of the user.
     * @param userId The UUID of the user.
     * @return An Optional containing the user if found, or empty otherwise.
     */
    Optional<User> findByTenantIdAndId(String tenantId, String userId);

    /**
     * Checks if a user with the given email exists.
     * @param email The email to check.
     * @return true if a user with the email exists, false otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Deletes a user by their ID.
     * @param id The UUID of the user to delete.
     */
    void deleteById(String id);
} 