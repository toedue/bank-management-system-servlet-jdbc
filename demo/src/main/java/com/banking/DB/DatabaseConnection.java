package com.banking.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {
    // banking_system 
    private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_system?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // open a connection to the database 
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
