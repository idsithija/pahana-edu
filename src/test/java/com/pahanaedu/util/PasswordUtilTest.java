package com.pahanaedu.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for PasswordUtil
 * Tests academic-compliant SHA-256 password hashing functionality
 */
@DisplayName("Password Utility Tests")
class PasswordUtilTest {
    
    private String testPassword;
    private String weakPassword;
    private String strongPassword;
    
    @BeforeEach
    void setUp() {
        testPassword = "testpass123";
        weakPassword = "weak";
        strongPassword = "StrongPass123!";
    }
    
    @Test
    @DisplayName("Should hash password with SHA-256 and salt")
    void testHashPassword() {
        // Test basic hashing
        String hashedPassword = PasswordUtil.hashPassword(testPassword);
        
        assertNotNull(hashedPassword, "Hashed password should not be null");
        assertNotEquals(testPassword, hashedPassword, "Hashed password should be different from plain text");
        assertTrue(hashedPassword.length() > 0, "Hashed password should not be empty");
        
        // Test that same password produces different hashes (due to salt)
        String hashedPassword2 = PasswordUtil.hashPassword(testPassword);
        assertNotEquals(hashedPassword, hashedPassword2, "Same password should produce different hashes due to salt");
    }
    
    @Test
    @DisplayName("Should throw exception for null password")
    void testHashPasswordWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.hashPassword(null);
        }, "Should throw IllegalArgumentException for null password");
    }
    
    @Test
    @DisplayName("Should verify password correctly")
    void testVerifyPassword() {
        // Hash a password
        String hashedPassword = PasswordUtil.hashPassword(testPassword);
        
        // Verify correct password
        assertTrue(PasswordUtil.verifyPassword(testPassword, hashedPassword), 
                  "Should verify correct password");
        
        // Verify incorrect password
        assertFalse(PasswordUtil.verifyPassword("wrongpassword", hashedPassword), 
                   "Should not verify incorrect password");
    }
    
    @Test
    @DisplayName("Should handle null values in verification")
    void testVerifyPasswordWithNulls() {
        String hashedPassword = PasswordUtil.hashPassword(testPassword);
        
        // Test null plain text password
        assertFalse(PasswordUtil.verifyPassword(null, hashedPassword), 
                   "Should return false for null plain text password");
        
        // Test null hashed password
        assertFalse(PasswordUtil.verifyPassword(testPassword, null), 
                   "Should return false for null hashed password");
        
        // Test both null
        assertFalse(PasswordUtil.verifyPassword(null, null), 
                   "Should return false for both null parameters");
    }
    
    @Test
    @DisplayName("Should generate random passwords")
    void testGenerateRandomPassword() {
        int passwordLength = 10;
        String randomPassword = PasswordUtil.generateRandomPassword(passwordLength);
        
        assertNotNull(randomPassword, "Generated password should not be null");
        assertEquals(passwordLength, randomPassword.length(), 
                    "Generated password should have correct length");
        
        // Generate another password and ensure they are different
        String anotherPassword = PasswordUtil.generateRandomPassword(passwordLength);
        assertNotEquals(randomPassword, anotherPassword, 
                       "Generated passwords should be different");
    }
    
    @Test
    @DisplayName("Should validate minimum password length for generation")
    void testGenerateRandomPasswordMinLength() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.generateRandomPassword(5);
        }, "Should throw exception for password length less than 6");
        
        // Test valid minimum length
        assertDoesNotThrow(() -> {
            PasswordUtil.generateRandomPassword(6);
        }, "Should not throw exception for password length of 6");
    }
    
    @Test
    @DisplayName("Should validate password strength correctly")
    void testIsPasswordStrong() {
        // Test strong password
        assertTrue(PasswordUtil.isPasswordStrong(strongPassword), 
                  "Should recognize strong password");
        
        // Test weak password (too short)
        assertFalse(PasswordUtil.isPasswordStrong(weakPassword), 
                   "Should recognize weak password as not strong");
        
        // Test password without uppercase
        assertFalse(PasswordUtil.isPasswordStrong("lowercase123!"), 
                   "Should recognize password without uppercase as not strong");
        
        // Test password without lowercase
        assertFalse(PasswordUtil.isPasswordStrong("UPPERCASE123!"), 
                   "Should recognize password without lowercase as not strong");
        
        // Test password without numbers
        assertFalse(PasswordUtil.isPasswordStrong("NoNumbers!"), 
                   "Should recognize password without numbers as not strong");
        
        // Test password without special characters
        assertFalse(PasswordUtil.isPasswordStrong("NoSpecial123"), 
                   "Should recognize password without special characters as not strong");
        
        // Test null password
        assertFalse(PasswordUtil.isPasswordStrong(null), 
                   "Should recognize null password as not strong");
    }
    
    @Test
    @DisplayName("Should calculate password strength score")
    void testGetPasswordStrength() {
        // Test null password
        assertEquals(0, PasswordUtil.getPasswordStrength(null), 
                    "Null password should have strength score of 0");
        
        // Test very weak password (should have 1 point for length >= 8 is false, but has lowercase)
        assertEquals(1, PasswordUtil.getPasswordStrength("weak"), 
                    "Very weak password should have low strength score");
        
        // Test medium password (length>=8, uppercase, lowercase, digit = 4 points)
        assertEquals(4, PasswordUtil.getPasswordStrength("Password123"), 
                    "Medium password should have high strength score");
        
        // Test strong password
        assertEquals(4, PasswordUtil.getPasswordStrength(strongPassword), 
                    "Strong password should have high strength score");
    }
    
    @Test
    @DisplayName("Should handle edge cases in password operations")
    void testPasswordEdgeCases() {
        // Test empty string password
        String emptyPassword = "";
        String hashedEmpty = PasswordUtil.hashPassword(emptyPassword);
        assertTrue(PasswordUtil.verifyPassword(emptyPassword, hashedEmpty), 
                  "Should handle empty password correctly");
        
        // Test very long password
        String longPassword = "a".repeat(1000);
        String hashedLong = PasswordUtil.hashPassword(longPassword);
        assertTrue(PasswordUtil.verifyPassword(longPassword, hashedLong), 
                  "Should handle very long password correctly");
        
        // Test password with special characters
        String specialPassword = "Test@#$%^&*()_+-=[]{}|;:,.<>?`~";
        String hashedSpecial = PasswordUtil.hashPassword(specialPassword);
        assertTrue(PasswordUtil.verifyPassword(specialPassword, hashedSpecial), 
                  "Should handle password with special characters correctly");
    }
    
    @Test
    @DisplayName("Should be consistent with known test vectors")
    void testKnownPasswordVectors() {
        // Test with known passwords used in the application
        String adminPassword = "admin123";
        String operatorPassword = "operator123";
        
        String hashedAdmin = PasswordUtil.hashPassword(adminPassword);
        String hashedOperator = PasswordUtil.hashPassword(operatorPassword);
        
        // Verify the passwords work correctly
        assertTrue(PasswordUtil.verifyPassword(adminPassword, hashedAdmin), 
                  "Admin password should verify correctly");
        assertTrue(PasswordUtil.verifyPassword(operatorPassword, hashedOperator), 
                  "Operator password should verify correctly");
        
        // Cross-verify they don't match each other
        assertFalse(PasswordUtil.verifyPassword(adminPassword, hashedOperator), 
                   "Admin password should not verify against operator hash");
        assertFalse(PasswordUtil.verifyPassword(operatorPassword, hashedAdmin), 
                   "Operator password should not verify against admin hash");
    }
}