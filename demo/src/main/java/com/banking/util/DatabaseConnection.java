package com.banking.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Helper class that knows how to connect to the MySQL database.
 *
 * Other classes call getConnection() when they need to talk to
 * the database. Keeping this code in one place makes it easier
 * to change later.
 */
public class DatabaseConnection {

    // URL of the database we want to connect to.
    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/banking_system?useSSL=false&serverTimezone=UTC";

    // Database username and password.
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    /**
     * Opens a new connection to the database.
     *
     * @return a live Connection object we can use for SQL queries
     * @throws SQLException if something goes wrong while connecting
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Make sure the MySQL driver class is loaded.
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Ask DriverManager to open the connection for us.
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            // Wrap the error so calling code only has to handle SQLException.
            throw new SQLException("MySQL Driver not found", e);
        }
    }
}

