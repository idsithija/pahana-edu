package com.pahanaedu.service.impl;

import com.pahanaedu.BaseTestCase;
import com.pahanaedu.dao.UserDAO;
import com.pahanaedu.dao.impl.UserDAOImpl;
import com.pahanaedu.model.User;
import com.pahanaedu.service.UserService;
import com.pahanaedu.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Arrays;
import java.util.List;

/**
 * JUnit test class for UserServiceImpl
 * Tests business logic and service layer functionality
 */
@DisplayName("User Service Implementation Tests")
class UserServiceImplTest extends BaseTestCase {
    
    private UserService userService;
    
    @Mock
    private UserDAO mockUserDAO;
    
    private User testUser;
    private String testUsername;
    private String testPassword;
    private String testPasswordHash;
    
    @BeforeEach
    protected void setUp() {
        super.setUp();
        MockitoAnnotations.openMocks(this);
        
        userService = new UserServiceImpl();
        testUsername = "testuser";
        testPassword = "testpass123";
        testPasswordHash = PasswordUtil.hashPassword(testPassword);
        testUser = new User(testUsername, testPasswordHash, User.UserRole.OPERATOR);
    }
    
    @Test
    @DisplayName("Should authenticate user with valid credentials")
    void testAuthenticateUserSuccess() {
        // Mock DAO to return user
        when(mockUserDAO.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        
        // Test authentication
        Optional<User> result = userService.authenticateUser(testUsername, testPassword);
        
        assertTrue(result.isPresent(), "Should authenticate user successfully");
        assertEquals(testUsername, result.get().getUsername(), "Should return correct user");
        
        verify(mockUserDAO).findByUsername(testUsername);
    }
    
    @Test
    @DisplayName("Should not authenticate user with invalid password")
    void testAuthenticateUserInvalidPassword() {
        // Mock DAO to return user
        when(mockUserDAO.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        
        // Test authentication with wrong password
        Optional<User> result = userService.authenticateUser(testUsername, "wrongpassword");
        
        assertFalse(result.isPresent(), "Should not authenticate user with invalid password");
        
        verify(mockUserDAO).findByUsername(testUsername);
    }
    
    @Test
    @DisplayName("Should not authenticate non-existent user")
    void testAuthenticateUserNotFound() {
        // Mock DAO to return empty optional
        when(mockUserDAO.findByUsername(testUsername)).thenReturn(Optional.empty());
        
        // Test authentication
        Optional<User> result = userService.authenticateUser(testUsername, testPassword);
        
        assertFalse(result.isPresent(), "Should not authenticate non-existent user");
        
        verify(mockUserDAO).findByUsername(testUsername);
    }
    
    @Test
    @DisplayName("Should not authenticate inactive user")
    void testAuthenticateInactiveUser() {
        // Set user as inactive
        testUser.setActive(false);
        
        // Mock DAO to return inactive user
        when(mockUserDAO.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        
        // Test authentication
        Optional<User> result = userService.authenticateUser(testUsername, testPassword);
        
        assertFalse(result.isPresent(), "Should not authenticate inactive user");
        
        verify(mockUserDAO).findByUsername(testUsername);
    }
    
    @Test
    @DisplayName("Should handle null or empty credentials")
    void testAuthenticateUserNullCredentials() {
        // Test null username
        Optional<User> result1 = userService.authenticateUser(null, testPassword);
        assertFalse(result1.isPresent(), "Should not authenticate with null username");
        
        // Test null password
        Optional<User> result2 = userService.authenticateUser(testUsername, null);
        assertFalse(result2.isPresent(), "Should not authenticate with null password");
        
        // Test empty username
        Optional<User> result3 = userService.authenticateUser("", testPassword);
        assertFalse(result3.isPresent(), "Should not authenticate with empty username");
        
        // Test empty password
        Optional<User> result4 = userService.authenticateUser(testUsername, "");
        assertFalse(result4.isPresent(), "Should not authenticate with empty password");
        
        // Verify no DAO calls were made
        verifyNoInteractions(mockUserDAO);
    }
    
    @Test
    @DisplayName("Should create user with valid data")
    void testCreateUserSuccess() {
        String newUsername = "newuser";
        String newPasswordHash = PasswordUtil.hashPassword("newpass123");
        User.UserRole newRole = User.UserRole.ADMIN;
        User newUser = new User(newUsername, newPasswordHash, newRole);
        
        // Mock DAO responses
        when(mockUserDAO.usernameExists(newUsername)).thenReturn(false);
        doNothing().when(mockUserDAO).save(any(User.class));
        
        // Test user creation
        userService.createUser(newUser);
        
        verify(mockUserDAO).usernameExists(newUsername);
        verify(mockUserDAO).save(any(User.class));
    }
    
    @Test
    @DisplayName("Should not create user with existing username")
    void testCreateUserDuplicateUsername() {
        // Mock DAO to indicate username exists
        when(mockUserDAO.usernameExists(testUsername)).thenReturn(true);
        
        User duplicateUser = new User(testUsername, PasswordUtil.hashPassword("password123"), User.UserRole.OPERATOR);
        
        // Test user creation
        assertThrows(RuntimeException.class, () -> {
            userService.createUser(duplicateUser);
        }, "Should throw exception for duplicate username");
        
        verify(mockUserDAO).usernameExists(testUsername);
        verify(mockUserDAO, never()).save(any(User.class));
    }
    
    @Test
    @DisplayName("Should create user with proper password hash")
    void testCreateUserPasswordHashing() {
        String newUsername = "newuser";
        String plainPassword = "testpass123";
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        User newUser = new User(newUsername, hashedPassword, User.UserRole.OPERATOR);
        
        // Mock username availability
        when(mockUserDAO.usernameExists(newUsername)).thenReturn(false);
        doNothing().when(mockUserDAO).save(any(User.class));
        
        // Test user creation
        userService.createUser(newUser);
        
        // Verify password is properly hashed
        assertNotEquals(plainPassword, newUser.getPasswordHash(), "Password should be hashed");
        assertTrue(PasswordUtil.verifyPassword(plainPassword, newUser.getPasswordHash()), "Password should verify correctly");
        
        verify(mockUserDAO).usernameExists(newUsername);
        verify(mockUserDAO).save(any(User.class));
    }
    
    @Test
    @DisplayName("Should validate required parameters for user creation")
    void testCreateUserParameterValidation() {
        // Test null user
        assertThrows(RuntimeException.class, () -> {
            userService.createUser(null);
        }, "Should throw exception for null user");
        
        verifyNoInteractions(mockUserDAO);
    }
    
    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser() {
        // Mock DAO response
        when(mockUserDAO.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        doNothing().when(mockUserDAO).update(testUser);
        
        // Test user update
        userService.updateUser(testUser);
        
        verify(mockUserDAO).findByUsername(testUsername);
        verify(mockUserDAO).update(testUser);
    }
    
    @Test
    @DisplayName("Should validate parameters for user update")
    void testUpdateUserValidation() {
        // Test null user
        assertThrows(RuntimeException.class, () -> {
            userService.updateUser(null);
        }, "Should throw exception for null user");
        
        // Test user not found
        User nonExistentUser = new User("nonexistent", "hash", User.UserRole.OPERATOR);
        when(mockUserDAO.findByUsername("nonexistent")).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> {
            userService.updateUser(nonExistentUser);
        }, "Should throw exception for non-existent user");
        
        verify(mockUserDAO).findByUsername("nonexistent");
    }
    
    @Test
    @DisplayName("Should update password successfully")
    void testUpdatePassword() {
        String newPasswordHash = PasswordUtil.hashPassword("newpass123");
        
        // Mock DAO responses
        when(mockUserDAO.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        doNothing().when(mockUserDAO).update(any(User.class));
        
        // Test password update
        userService.updatePassword(testUsername, newPasswordHash);
        
        verify(mockUserDAO).findByUsername(testUsername);
        verify(mockUserDAO).update(any(User.class));
    }
    
    @Test
    @DisplayName("Should validate user exists for password update")
    void testUpdatePasswordUserNotFound() {
        String newPasswordHash = PasswordUtil.hashPassword("newpass123");
        
        // Mock DAO to return empty optional
        when(mockUserDAO.findByUsername(testUsername)).thenReturn(Optional.empty());
        
        // Test password update for non-existent user
        assertThrows(RuntimeException.class, () -> {
            userService.updatePassword(testUsername, newPasswordHash);
        }, "Should throw exception for non-existent user");
        
        verify(mockUserDAO).findByUsername(testUsername);
        verify(mockUserDAO, never()).update(any(User.class));
    }
    
    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser() {
        // Mock DAO responses
        when(mockUserDAO.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        doNothing().when(mockUserDAO).delete(testUsername);
        
        // Test user deletion
        userService.deleteUser(testUsername);
        
        verify(mockUserDAO).findByUsername(testUsername);
        verify(mockUserDAO).delete(testUsername);
    }
    
    @Test
    @DisplayName("Should find user by username")
    void testFindByUsername() {
        // Mock DAO response
        when(mockUserDAO.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        
        // Test find by username
        Optional<User> result = userService.findByUsername(testUsername);
        
        assertTrue(result.isPresent(), "Should find user by username");
        assertEquals(testUsername, result.get().getUsername(), "Should return correct user");
        
        verify(mockUserDAO).findByUsername(testUsername);
    }
    
    @Test
    @DisplayName("Should check username existence")
    void testUsernameExists() {
        // Mock DAO responses
        when(mockUserDAO.usernameExists(testUsername)).thenReturn(true);
        when(mockUserDAO.usernameExists("availableusername")).thenReturn(false);
        
        // Test existing username
        assertTrue(userService.usernameExists(testUsername), 
                   "Should return true for existing username");
        
        // Test non-existing username
        assertFalse(userService.usernameExists("availableusername"), 
                  "Should return false for non-existing username");
        
        verify(mockUserDAO).usernameExists(testUsername);
        verify(mockUserDAO).usernameExists("availableusername");
    }
    
    @Test
    @DisplayName("Should toggle user status")
    void testToggleUserStatus() {
        // Mock DAO responses
        when(mockUserDAO.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        doNothing().when(mockUserDAO).update(any(User.class));
        
        boolean originalStatus = testUser.isActive();
        
        // Test status toggle
        userService.toggleUserStatus(testUsername);
        
        verify(mockUserDAO).findByUsername(testUsername);
        verify(mockUserDAO).update(any(User.class));
        
        // Verify status was toggled
        assertEquals(!originalStatus, testUser.isActive(), "User status should be toggled");
    }
}