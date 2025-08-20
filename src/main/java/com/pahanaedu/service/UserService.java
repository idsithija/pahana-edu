package com.pahanaedu.service;

import com.pahanaedu.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for User business logic
 */
public interface UserService {

    /**
     * Get all users
     */
    List<User> getAllUsers();

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Search users by username
     */
    List<User> searchUsers(String searchTerm);

    /**
     * Create new user
     */
    void createUser(User user);

    /**
     * Update existing user
     */
    void updateUser(User user);

    /**
     * Delete user by username
     */
    void deleteUser(String username);

    /**
     * Check if username exists
     */
    boolean usernameExists(String username);

    /**
     * Toggle user active status
     */
    void toggleUserStatus(String username);

    /**
     * Update user password
     */
    void updatePassword(String username, String passwordHash);

    /**
     * Find users by role
     */
    List<User> findByRole(User.UserRole role);

    /**
     * Find active users
     */
    List<User> findActiveUsers();
    
    /**
     * Authenticate user with username and password
     */
    Optional<User> authenticateUser(String username, String password);
}
