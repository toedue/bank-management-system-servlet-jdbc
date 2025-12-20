package com.banking.model;

/**
 * Simple class that represents a user who can log in to the system.
 *
 * A user has an email, password and a role (for example "admin" or "user").
 * Only the data is stored here – the database logic lives in the DAO classes.
 */
public class User {

    // Unique id from the database.
    private int id;

    // Email used for login.
    private String email;

    // Hashed password (never store plain passwords in a real app).
    private String password;

    // Role string, such as "admin" or "user".
    private String role;

    // Empty constructor.
    public User() {
    }

    // Constructor that fills every field at once.
    public User(int id, String email, String password, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

