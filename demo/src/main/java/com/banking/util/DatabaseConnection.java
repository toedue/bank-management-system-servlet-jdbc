package com.banking.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DATABASE CONNECTION UTILITY
 * This class is like a "bridge" between our Java code and the MySQL database.
 * We keep it in one place so we don't have to repeat the same setup code everywhere.
 */
public class DatabaseConnection {

    // 1. Where is the database?
    // localhost = your own computer
    // 3306 = the default "gate" (port) MySQL uses
    // banking_system = the name of the database we created
    private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_system?useSSL=false&serverTimezone=UTC";

    // 2. Who is accessing it?
    // 'root' is the default username for most local setups
    private static final String DB_USER = "root";
    
    // 3. What is the password?
    // If you haven't set a password for MySQL, leave this empty ""
    private static final String DB_PASSWORD = "";

    /**
     * This method opens a connection to the database.
     * We call this whenever we need to save or get information.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // STEP A: Tell Java to use the MySQL "Driver" (the software that talks to MySQL)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // STEP B: Actually open the connection using our settings above
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
        } catch (ClassNotFoundException e) {
            // This happens if the MySQL Driver library is missing
            throw new SQLException("Error: MySQL Database Driver was not found!", e);
        }
    }
}
