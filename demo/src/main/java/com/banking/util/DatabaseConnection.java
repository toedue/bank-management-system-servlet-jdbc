package com.banking.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// This class helps us connect to the database
// We put all database connection code here so we can use it everywhere
public class DatabaseConnection {

    // This is where the database is located
    // localhost means the database is on your computer
    // 3306 is the port number
    // banking_system is the name of our database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_system?useSSL=false&serverTimezone=UTC";

    // This is the username to login to the database
    private static final String DB_USER = "root";
    
    // This is the password to login to the database
    // Leave it empty if you don't have a password
    private static final String DB_PASSWORD = "";

    // This method connects to the database and returns the connection
    public static Connection getConnection() throws SQLException {
        try {
            // Tell Java where to find the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database using the URL, username, and password
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
        } catch (ClassNotFoundException e) {
            // If we can't find the MySQL driver, show an error
            throw new SQLException("MySQL Driver not found", e);
        }
    }
}

