package com.pahanaedu.util;

/**
 * Utility to generate new password hashes with academic-compliant SHA-256 implementation
 */
public class GenerateNewHashes {
    
    public static void main(String[] args) {
        System.out.println("Generating academic-compliant password hashes...");
        
        String adminPassword = "admin123";
        String operatorPassword = "operator123";
        
        try {
            String adminHash = PasswordUtil.hashPassword(adminPassword);
            String operatorHash = PasswordUtil.hashPassword(operatorPassword);
            
            System.out.println("Admin password '" + adminPassword + "' hash: " + adminHash);
            System.out.println("Operator password '" + operatorPassword + "' hash: " + operatorHash);
            
            // Verify the hashes work
            boolean adminVerified = PasswordUtil.verifyPassword(adminPassword, adminHash);
            boolean operatorVerified = PasswordUtil.verifyPassword(operatorPassword, operatorHash);
            
            System.out.println("Admin hash verification: " + adminVerified);
            System.out.println("Operator hash verification: " + operatorVerified);
            
            System.out.println("\nSQL Updates:");
            System.out.println("UPDATE users SET password_hash = '" + adminHash + "' WHERE username = 'admin';");
            System.out.println("UPDATE users SET password_hash = '" + operatorHash + "' WHERE username = 'operator';");
            
        } catch (Exception e) {
            System.err.println("Error generating hashes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}