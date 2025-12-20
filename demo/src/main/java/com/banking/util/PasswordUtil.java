package com.banking.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Helper class for working with passwords.
 *
 * In this project we use a simple MD5 hash so we never store
 * the plain text password in the database. (For real systems,
 * use a stronger algorithm like bcrypt or Argon2.)
 */
public class PasswordUtil {

    /**
     * Turns a plain text password into an MD5 hash string.
     *
     * @param password the password typed by the user
     * @return a hexadecimal string representation of the MD5 hash
     */
    public static String hashPassword(String password) {
        try {
            // Create a MessageDigest object that uses MD5.
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            // Convert the password into bytes and calculate the hash.
            byte[] hashBytes = messageDigest.digest(password.getBytes());

            // Turn each byte into a two‑digit hex string.
            StringBuilder hexString = new StringBuilder();
            for (byte singleByte : hashBytes) {
                hexString.append(String.format("%02x", singleByte));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // This should not happen with MD5, but we handle it anyway.
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    /**
     * Checks if a plain password matches a stored hash.
     *
     * @param password the plain password typed by the user
     * @param hash     the already stored hash from the database
     * @return true if they match, false otherwise
     */
    public static boolean verifyPassword(String password, String hash) {
        String calculatedHash = hashPassword(password);
        return calculatedHash.equals(hash);
    }
}

