package com.pahanaedu.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHash {
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            throw new IllegalArgumentException("Invalid hashed password format");
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
