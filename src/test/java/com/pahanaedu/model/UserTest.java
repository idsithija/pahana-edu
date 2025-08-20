package com.pahanaedu.model;

import com.pahanaedu.BaseTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for User model
 * Tests User entity functionality and business logic
 */
@DisplayName("User Model Tests")
class UserTest extends BaseTestCase {
    
    private User testUser;
    private String testUsername;
    private String testPasswordHash;
    
    @BeforeEach
    protected void setUp() {
        super.setUp();
        testUsername = "testuser";
        testPasswordHash = "hashedpassword123";
        testUser = new User(testUsername, testPasswordHash, User.UserRole.OPERATOR);
    }
    
    @Test
    @DisplayName("Should create user with valid parameters")
    void testUserCreation() {
        assertNotNull(testUser, "User should be created successfully");
        assertEquals(testUsername, testUser.getUsername(), "Username should match");
        assertEquals(testPasswordHash, testUser.getPasswordHash(), "Password hash should match");
        assertEquals(User.UserRole.OPERATOR, testUser.getRole(), "Role should match");
        assertTrue(testUser.isActive(), "User should be active by default");
        assertNotNull(testUser.getCreatedDate(), "Created date should be set");
    }
    
    @Test
    @DisplayName("Should handle all user roles correctly")
    void testUserRoles() {
        // Test ADMIN role
        User adminUser = new User("admin", "hash", User.UserRole.ADMIN);
        assertEquals(User.UserRole.ADMIN, adminUser.getRole(), "Admin role should be set correctly");
        
        // Test OPERATOR role
        User operatorUser = new User("operator", "hash", User.UserRole.OPERATOR);
        assertEquals(User.UserRole.OPERATOR, operatorUser.getRole(), "Operator role should be set correctly");
        
        // Test role enum values
        User.UserRole[] roles = User.UserRole.values();
        assertEquals(2, roles.length, "Should have exactly 2 user roles");
        assertTrue(java.util.Arrays.asList(roles).contains(User.UserRole.ADMIN), "Should contain ADMIN role");
        assertTrue(java.util.Arrays.asList(roles).contains(User.UserRole.OPERATOR), "Should contain OPERATOR role");
    }
    
    @Test
    @DisplayName("Should persist user to database")
    void testUserPersistence() {
        if (!isTestEnvironmentReady()) {
            System.out.println("Skipping database test - test environment not ready");
            return;
        }
        
        beginTransaction();
        
        try {
            // Persist user
            entityManager.persist(testUser);
            commitTransaction();
            
            // Clear cache and retrieve
            clearEntityManager();
            User retrievedUser = entityManager.find(User.class, testUsername);
            
            assertNotNull(retrievedUser, "User should be retrieved from database");
            assertEquals(testUser.getUsername(), retrievedUser.getUsername(), "Username should match");
            assertEquals(testUser.getPasswordHash(), retrievedUser.getPasswordHash(), "Password hash should match");
            assertEquals(testUser.getRole(), retrievedUser.getRole(), "Role should match");
            assertEquals(testUser.isActive(), retrievedUser.isActive(), "Active status should match");
            
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }
    
    @Test
    @DisplayName("Should update user properties correctly")
    void testUserUpdate() {
        // Test setting active status
        testUser.setActive(false);
        assertFalse(testUser.isActive(), "User should be set as inactive");
        
        testUser.setActive(true);
        assertTrue(testUser.isActive(), "User should be set as active");
        
        // Test setting password hash
        String newPasswordHash = "newhashedpassword456";
        testUser.setPasswordHash(newPasswordHash);
        assertEquals(newPasswordHash, testUser.getPasswordHash(), "Password hash should be updated");
        
        // Test setting role
        testUser.setRole(User.UserRole.ADMIN);
        assertEquals(User.UserRole.ADMIN, testUser.getRole(), "Role should be updated");
    }
    
    @Test
    @DisplayName("Should handle username constraints")
    void testUsernameConstraints() {
        // Test username length validation in entity
        assertDoesNotThrow(() -> {
            new User("a", "hash", User.UserRole.OPERATOR);
        }, "Should accept single character username");
        
        assertDoesNotThrow(() -> {
            new User("a".repeat(50), "hash", User.UserRole.OPERATOR);
        }, "Should accept username up to 50 characters");
    }
    
    @Test
    @DisplayName("Should handle password hash constraints")
    void testPasswordHashConstraints() {
        // Test that password hash can be long (for SHA-256 + salt)
        String longHash = "a".repeat(255);
        User userWithLongHash = new User("testlong", longHash, User.UserRole.OPERATOR);
        assertEquals(longHash, userWithLongHash.getPasswordHash(), "Should handle long password hash");
    }
    
    @Test
    @DisplayName("Should implement equals and hashCode correctly")
    void testEqualsAndHashCode() {
        // Create two users with same username
        User user1 = new User(testUsername, testPasswordHash, User.UserRole.OPERATOR);
        User user2 = new User(testUsername, "differenthash", User.UserRole.ADMIN);
        
        // Test equals based on username (primary key)
        assertEquals(user1, user2, "Users with same username should be equal");
        assertEquals(user1.hashCode(), user2.hashCode(), "Users with same username should have same hash code");
        
        // Test with different username
        User user3 = new User("different", testPasswordHash, User.UserRole.OPERATOR);
        assertNotEquals(user1, user3, "Users with different usernames should not be equal");
        
        // Test with null
        assertNotEquals(user1, null, "User should not equal null");
        
        // Test with different class
        assertNotEquals(user1, "string", "User should not equal different class");
    }
    
    @Test
    @DisplayName("Should implement toString correctly")
    void testToString() {
        String toString = testUser.toString();
        
        assertNotNull(toString, "toString should not return null");
        assertTrue(toString.contains(testUsername), "toString should contain username");
        assertTrue(toString.contains(User.UserRole.OPERATOR.toString()), "toString should contain role");
        assertFalse(toString.contains(testPasswordHash), "toString should not contain password hash for security");
    }
    
    @Test
    @DisplayName("Should handle null values appropriately")
    void testNullHandling() {
        // Test creating user with null parameters - should be handled by JPA validation
        assertThrows(Exception.class, () -> {
            new User(null, testPasswordHash, User.UserRole.OPERATOR);
        }, "Should throw exception for null username");
        
        assertThrows(Exception.class, () -> {
            new User(testUsername, null, User.UserRole.OPERATOR);
        }, "Should throw exception for null password hash");
        
        assertThrows(Exception.class, () -> {
            new User(testUsername, testPasswordHash, null);
        }, "Should throw exception for null role");
    }
    
    @Test
    @DisplayName("Should validate user business logic")
    void testUserBusinessLogic() {
        // Test default state
        assertTrue(testUser.isActive(), "New user should be active by default");
        assertNotNull(testUser.getCreatedDate(), "New user should have created date");
        
        // Test role-based logic
        User adminUser = new User("admin", "hash", User.UserRole.ADMIN);
        assertEquals(User.UserRole.ADMIN, adminUser.getRole(), "Admin user should have ADMIN role");
        
        User operatorUser = new User("operator", "hash", User.UserRole.OPERATOR);
        assertEquals(User.UserRole.OPERATOR, operatorUser.getRole(), "Operator user should have OPERATOR role");
    }
}