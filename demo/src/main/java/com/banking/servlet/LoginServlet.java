package com.banking.servlet;

import com.banking.util.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// This servlet handles when someone tries to login
public class LoginServlet extends HttpServlet {
    
    // This runs when someone visits the login page
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Show the login page
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
    
    // This runs when someone submits the login form
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get the email and password that the user typed
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // Check if email and password are not empty
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Please enter email and password");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        
        // Connect to the database
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            // Get connection to database
            connection = DatabaseConnection.getConnection();
            
            // Create a query to find the user by email
            String sql = "SELECT * FROM users WHERE email = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            
            // Run the query
            resultSet = statement.executeQuery();
            
            // Check if we found a user
            if (resultSet.next()) {
                // Get the password from database
                String storedPassword = resultSet.getString("password");
                
                // Check if the password matches
                if (password.equals(storedPassword)) {
                    // Password is correct! Get user info
                    int userId = resultSet.getInt("id");
                    String userEmail = resultSet.getString("email");
                    String userRole = resultSet.getString("role");
                    
                    // Save user info in session so we remember they are logged in
                    HttpSession session = request.getSession();
                    session.setAttribute("userId", userId);
                    session.setAttribute("userEmail", userEmail);
                    session.setAttribute("userRole", userRole);
                    
                    // If user is a customer (not admin), get their account info
                    if ("user".equals(userRole)) {
                        resultSet.close();
                        statement.close();
                        
                        // Get customer account info
                        String customerSql = "SELECT * FROM customers WHERE user_id = ?";
                        statement = connection.prepareStatement(customerSql);
                        statement.setInt(1, userId);
                        resultSet = statement.executeQuery();
                        
                        if (resultSet.next()) {
                            // Save customer info in session
                            session.setAttribute("customerId", resultSet.getInt("id"));
                            session.setAttribute("accountNumber", resultSet.getString("account_number"));
                            session.setAttribute("customerName", resultSet.getString("name"));
                            session.setAttribute("customerEmail", resultSet.getString("email"));
                            session.setAttribute("customerPhone", resultSet.getString("phone"));
                            session.setAttribute("customerAddress", resultSet.getString("address"));
                            session.setAttribute("balance", resultSet.getDouble("balance"));
                        }
                    }
                    
                    // Send user to their dashboard
                    if ("admin".equals(userRole)) {
                        response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/user/dashboard.jsp");
                    }
                    return;
                }
            }
            
            // If we get here, login failed
            request.setAttribute("error", "Invalid email or password");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            
        } catch (Exception e) {
            // If something goes wrong, show error
            e.printStackTrace();
            request.setAttribute("error", "Login failed. Please try again.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } finally {
            // Always close the database connection
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

