package com.banking.servlet;

import com.banking.util.DatabaseConnection;
import com.banking.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// This servlet lets admin add new customers
public class AddCustomerServlet extends HttpServlet {
    
    // This runs when admin visits the add customer page
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if admin is logged in
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        request.getRequestDispatcher("/admin/addCustomer.jsp").forward(request, response);
    }
    
    // This runs when admin submits the add customer form
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if admin is logged in
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Get all the information from the form
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String balanceStr = request.getParameter("balance");
        
        // Check if name, email, and password are filled
        if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Please fill all required fields");
            request.getRequestDispatcher("/admin/addCustomer.jsp").forward(request, response);
            return;
        }
        
        // Check if password is at least 8 characters
        if (!PasswordUtil.isValidPassword(password)) {
            request.setAttribute("error", "Password must be at least 8 characters long");
            request.getRequestDispatcher("/admin/addCustomer.jsp").forward(request, response);
            return;
        }
        
        // Convert balance from string to number
        double balance = 0.0;
        try {
            balance = Double.parseDouble(balanceStr != null ? balanceStr : "0");
        } catch (NumberFormatException e) {
            balance = 0.0;
        }
        
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
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
                    request.getRequestDispatcher("/admin/addCustomer.jsp").forward(request, response);
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
                request.setAttribute("error", "Failed to add customer");
                request.getRequestDispatcher("/admin/addCustomer.jsp").forward(request, response);
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
                request.setAttribute("error", "Failed to add customer");
                request.getRequestDispatcher("/admin/addCustomer.jsp").forward(request, response);
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
            statement.setDouble(7, balance);
            
            rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                request.setAttribute("success", "Customer added successfully!");
            } else {
                request.setAttribute("error", "Failed to add customer");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to add customer");
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
        
        request.getRequestDispatcher("/admin/addCustomer.jsp").forward(request, response);
    }
}
