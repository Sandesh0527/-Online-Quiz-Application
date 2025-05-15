package com.quizapp.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password hashing and validation.
 */
public class PasswordUtil {
    private static final int LOG_ROUNDS = 10;
    
    /**
     * Hashes a password using BCrypt.
     *
     * @param plainTextPassword the password to hash
     * @return the hashed password
     */
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(LOG_ROUNDS));
    }
    
    /**
     * Checks if a plain text password matches a hashed password.
     *
     * @param plainTextPassword the plain text password to check
     * @param hashedPassword the hashed password to check against
     * @return true if the passwords match, false otherwise
     */
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}