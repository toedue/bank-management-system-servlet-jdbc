package com.banking.servlet;

import com.banking.util.DatabaseConnection;
import com.banking.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// This servlet handles when someone wants to create a new account
public class RegisterServlet extends HttpServlet {
    
    // This runs when someone visits the registration page
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Show the registration page
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }
    
    // This runs when someone submits the registration form
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get all the information the user typed
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        
        // Check if name, email, and password are filled
        if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Please fill all required fields");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        
        // Check if password is at least 8 characters
        if (!PasswordUtil.isValidPassword(password)) {
            request.setAttribute("error", "Password must be at least 8 characters long");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            // Connect to database
            connection = DatabaseConnection.getConnection();
            
            // Check if email already exists
            String checkEmailSql = "SELECT COUNT(*) FROM users WHERE email = ?";
            statement = connection.prepareStatement(checkEmailSql);
            statement.setString(1, email);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    request.setAttribute("error", "Email already registered");
                    request.getRequestDispatcher("/register.jsp").forward(request, response);
                    return;
                }
            }
            
            resultSet.close();
            statement.close();
            
            // Add new user to database
            String insertUserSql = "INSERT INTO users (email, password, role) VALUES (?, ?, ?)";
            statement = connection.prepareStatement(insertUserSql);
            statement.setString(1, email);
            statement.setString(2, password);
            statement.setString(3, "user");
            
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted == 0) {
                request.setAttribute("error", "Registration failed. Please try again.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }
            
            statement.close();
            
            // Get the user ID we just created
            String getUserSql = "SELECT id FROM users WHERE email = ?";
            statement = connection.prepareStatement(getUserSql);
            statement.setString(1, email);
            resultSet = statement.executeQuery();
            
            int userId = 0;
            if (resultSet.next()) {
                userId = resultSet.getInt("id");
            }
            
            resultSet.close();
            statement.close();
            
            if (userId == 0) {
                request.setAttribute("error", "Registration failed. Please try again.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }
            
            // Create customer account
            String accountNumber = "ACC" + System.currentTimeMillis();
            String insertCustomerSql = "INSERT INTO customers (account_number, user_id, name, email, phone, address, balance) VALUES (?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(insertCustomerSql);
            statement.setString(1, accountNumber);
            statement.setInt(2, userId);
            statement.setString(3, name);
            statement.setString(4, email);
            statement.setString(5, phone != null ? phone : "");
            statement.setString(6, address != null ? address : "");
            statement.setDouble(7, 0.0);
            
            rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                request.setAttribute("success", "Registration successful! Please login.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Registration failed. Please try again.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } finally {
            // Always close database connection
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
