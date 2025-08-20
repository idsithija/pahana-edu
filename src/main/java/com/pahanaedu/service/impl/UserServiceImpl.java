package com.pahanaedu.service.impl;

import com.pahanaedu.dao.UserDAO;
import com.pahanaedu.model.User;
import com.pahanaedu.service.UserService;
import com.pahanaedu.util.ServiceFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Implementation of UserService interface
 */
@Stateless
public class UserServiceImpl implements UserService {

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @EJB
    private UserDAO userDAO;

    public UserServiceImpl() {
        // Fallback for development environments where EJB injection might fail
        if (userDAO == null) {
            userDAO = ServiceFactory.getUserDAO();
        }
    }

    @Override
    public List<User> getAllUsers() {
        try {
            return userDAO.findAll();
        } catch (Exception e) {
            logger.severe("Error getting all users: " + e.getMessage());
            throw new RuntimeException("Error retrieving users", e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            return userDAO.findByUsername(username);
        } catch (Exception e) {
            logger.severe("Error finding user by username: " + e.getMessage());
            throw new RuntimeException("Error finding user", e);
        }
    }

    @Override
    public List<User> searchUsers(String searchTerm) {
        try {
            // Search by username (case-insensitive)
            return userDAO.findAll().stream()
                    .filter(user -> user.getUsername().toLowerCase().contains(searchTerm.toLowerCase()))
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.severe("Error searching users: " + e.getMessage());
            throw new RuntimeException("Error searching users", e);
        }
    }

    @Override
    public void createUser(User user) {
        try {
            if (usernameExists(user.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
            userDAO.save(user);
            logger.info("User created successfully: " + user.getUsername());
        } catch (Exception e) {
            logger.severe("Error creating user: " + e.getMessage());
            throw new RuntimeException("Error creating user", e);
        }
    }

    @Override
    public void updateUser(User user) {
        try {
            Optional<User> existingUser = userDAO.findByUsername(user.getUsername());
            if (!existingUser.isPresent()) {
                throw new IllegalArgumentException("User not found");
            }
            userDAO.update(user);
            logger.info("User updated successfully: " + user.getUsername());
        } catch (Exception e) {
            logger.severe("Error updating user: " + e.getMessage());
            throw new RuntimeException("Error updating user", e);
        }
    }

    @Override
    public void deleteUser(String username) {
        try {
            Optional<User> user = userDAO.findByUsername(username);
            if (!user.isPresent()) {
                throw new IllegalArgumentException("User not found");
            }
            userDAO.delete(username);
            logger.info("User deleted successfully: " + username);
        } catch (Exception e) {
            logger.severe("Error deleting user: " + e.getMessage());
            throw new RuntimeException("Error deleting user", e);
        }
    }

    @Override
    public boolean usernameExists(String username) {
        try {
            return userDAO.usernameExists(username);
        } catch (Exception e) {
            logger.severe("Error checking if username exists: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void toggleUserStatus(String username) {
        try {
            Optional<User> userOpt = userDAO.findByUsername(username);
            if (!userOpt.isPresent()) {
                throw new IllegalArgumentException("User not found");
            }

            User user = userOpt.get();
            user.setActive(!user.isActive());
            userDAO.update(user);
            logger.info("User status toggled for: " + username);
        } catch (Exception e) {
            logger.severe("Error toggling user status: " + e.getMessage());
            throw new RuntimeException("Error toggling user status", e);
        }
    }

    @Override
    public void updatePassword(String username, String passwordHash) {
        try {
            Optional<User> userOpt = userDAO.findByUsername(username);
            if (!userOpt.isPresent()) {
                throw new IllegalArgumentException("User not found");
            }

            User user = userOpt.get();
            user.setPasswordHash(passwordHash);
            userDAO.update(user);
            logger.info("Password updated for user: " + username);
        } catch (Exception e) {
            logger.severe("Error updating password: " + e.getMessage());
            throw new RuntimeException("Error updating password", e);
        }
    }

    @Override
    public List<User> findByRole(User.UserRole role) {
        try {
            return userDAO.findByRole(role);
        } catch (Exception e) {
            logger.severe("Error finding users by role: " + e.getMessage());
            throw new RuntimeException("Error finding users by role", e);
        }
    }

    @Override
    public List<User> findActiveUsers() {
        try {
            return userDAO.findActiveUsers();
        } catch (Exception e) {
            logger.severe("Error finding active users: " + e.getMessage());
            throw new RuntimeException("Error finding active users", e);
        }
    }
    
    @Override
    public Optional<User> authenticateUser(String username, String password) {
        try {
            Optional<User> userOpt = userDAO.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // Check if user is active and password matches
                if (user.isActive() && com.pahanaedu.util.PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                    return Optional.of(user);
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            logger.severe("Error authenticating user: " + e.getMessage());
            return Optional.empty();
        }
    }
}
