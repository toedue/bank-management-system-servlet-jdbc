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
import java.util.HashMap;
import java.util.Map;

// This servlet lets users update their profile information
public class UpdateProfileServlet extends HttpServlet {
    
    // This runs when user visits the update profile page
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || !"user".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Get customer info from database
        Integer userId = (Integer) session.getAttribute("userId");
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM customers WHERE user_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                // Put customer info in a map
                Map<String, Object> customer = new HashMap<>();
                customer.put("id", resultSet.getInt("id"));
                customer.put("accountNumber", resultSet.getString("account_number"));
                customer.put("userId", resultSet.getInt("user_id"));
                customer.put("name", resultSet.getString("name"));
                customer.put("email", resultSet.getString("email"));
                customer.put("phone", resultSet.getString("phone"));
                customer.put("address", resultSet.getString("address"));
                customer.put("balance", resultSet.getDouble("balance"));
                request.setAttribute("customer", customer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        request.getRequestDispatcher("/user/updateProfile.jsp").forward(request, response);
    }
    
    // This runs when user submits the update profile form
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || !"user".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Get information from the form
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        String accountNumber = (String) session.getAttribute("accountNumber");
        String email = (String) session.getAttribute("userEmail");
        
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean updated = false;
        
        try {
            connection = DatabaseConnection.getConnection();
            
            // Update customer info
            String updateCustomerSql = "UPDATE customers SET name = ?, phone = ?, address = ? WHERE account_number = ?";
            statement = connection.prepareStatement(updateCustomerSql);
            statement.setString(1, name != null ? name : "");
            statement.setString(2, phone != null ? phone : "");
            statement.setString(3, address != null ? address : "");
            statement.setString(4, accountNumber);
            
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                updated = true;
            }
            
            statement.close();
            
            // Update password if user provided one
            if (updated && password != null && !password.trim().isEmpty()) {
                // Check if password is at least 8 characters
                if (!PasswordUtil.isValidPassword(password)) {
                    request.setAttribute("error", "Password must be at least 8 characters long");
                    request.getRequestDispatcher("/user/updateProfile.jsp").forward(request, response);
                    return;
                }
                
                String updatePasswordSql = "UPDATE users SET password = ? WHERE email = ?";
                statement = connection.prepareStatement(updatePasswordSql);
                statement.setString(1, password);
                statement.setString(2, email);
                statement.executeUpdate();
                statement.close();
            }
            
            if (updated) {
                // Get updated customer info
                String getCustomerSql = "SELECT * FROM customers WHERE account_number = ?";
                statement = connection.prepareStatement(getCustomerSql);
                statement.setString(1, accountNumber);
                resultSet = statement.executeQuery();
                
                if (resultSet.next()) {
                    Map<String, Object> customer = new HashMap<>();
                    customer.put("id", resultSet.getInt("id"));
                    customer.put("accountNumber", resultSet.getString("account_number"));
                    customer.put("userId", resultSet.getInt("user_id"));
                    customer.put("name", resultSet.getString("name"));
                    customer.put("email", resultSet.getString("email"));
                    customer.put("phone", resultSet.getString("phone"));
                    customer.put("address", resultSet.getString("address"));
                    customer.put("balance", resultSet.getDouble("balance"));
                    request.setAttribute("customer", customer);
                }
                
                request.setAttribute("success", "Profile updated successfully!");
            } else {
                request.setAttribute("error", "Failed to update profile");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to update profile");
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        request.getRequestDispatcher("/user/updateProfile.jsp").forward(request, response);
    }
}
