package com.pahanaedu.dao;

import com.pahanaedu.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for User entity
 * Provides user-specific database operations
 */
public interface UserDAO extends BaseDAO<User, String> {
    
    /**
     * Find user by username and password hash
     * @param username Username
     * @param passwordHash Hashed password
     * @return Optional containing user if found and credentials match
     */
    Optional<User> findByUsernameAndPassword(String username, String passwordHash);
    
    /**
     * Find user by username
     * @param username Username
     * @return Optional containing user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find all active users
     * @return List of active users
     */
    List<User> findActiveUsers();
    
    /**
     * Find users by role
     * @param role User role (ADMIN, OPERATOR)
     * @return List of users with specified role
     */
    List<User> findByRole(User.UserRole role);
    
    /**
     * Check if username exists
     * @param username Username to check
     * @return true if username exists
     */
    boolean usernameExists(String username);
    
    /**
     * Activate or deactivate user
     * @param username Username
     * @param active Active status
     * @return Updated user
     */
    User updateActiveStatus(String username, boolean active);
    
    /**
     * Update user password
     * @param username Username
     * @param newPasswordHash New hashed password
     * @return Updated user
     */
    User updatePassword(String username, String newPasswordHash);
    
    /**
     * Delete user by username
     * @param username Username of user to delete
     */
    void delete(String username);
}

