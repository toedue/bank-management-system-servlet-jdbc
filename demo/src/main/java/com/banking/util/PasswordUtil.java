package com.banking.util;

// This class helps us check if a password is good enough
// We check if the password has at least 8 characters
public class PasswordUtil {

    // This method checks if the password is at least 8 characters long
    // If password is null or too short, return false
    // If password is 8 characters or more, return true
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        if (password.length() >= 8) {
            return true;
        }
        return false;
    }
}

