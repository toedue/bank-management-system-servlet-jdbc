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
import java.util.HashMap;
import java.util.Map;

// This servlet lets users update their profile information
public class UpdateProfileServlet extends HttpServlet {
    
    // This runs when user visits the update profile page
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");
        
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM customers WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            
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
            connection.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        request.getRequestDispatcher("/user/updateProfile.jsp").forward(request, response);
    }
    
    // This runs when user submits the update profile form
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        String accountNumber = (String) session.getAttribute("accountNumber");
        String email = (String) session.getAttribute("userEmail");
        
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        
        try {
            Connection connection = DatabaseConnection.getConnection();
            
            // Update customer details
            String updateCustomerSql = "UPDATE customers SET name = ?, phone = ?, address = ? WHERE account_number = ?";
            PreparedStatement statement = connection.prepareStatement(updateCustomerSql);
            statement.setString(1, name);
            statement.setString(2, phone);
            statement.setString(3, address);
            statement.setString(4, accountNumber);
            statement.executeUpdate();
            
            // Update password if provided
            if (password != null && !password.trim().isEmpty()) {
                String updatePasswordSql = "UPDATE users SET password = ? WHERE email = ?";
                PreparedStatement pwdStmt = connection.prepareStatement(updatePasswordSql);
                pwdStmt.setString(1, password);
                pwdStmt.setString(2, email);
                pwdStmt.executeUpdate();
            }
            
            connection.close();
            response.sendRedirect(request.getContextPath() + "/user/updateProfile.jsp?msg=updated");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/user/updateProfile.jsp?error=failed");
        }
    }
}
