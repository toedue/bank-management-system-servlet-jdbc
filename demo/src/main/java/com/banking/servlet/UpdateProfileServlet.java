package com.banking.servlet;

import com.banking.model.Customer;
import com.banking.model.User;
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

public class UpdateProfileServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"user".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Get customer from session or database
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            Integer userId = (Integer) session.getAttribute("userId");
            try {
                Connection connection = DatabaseConnection.getConnection();
                String sql = "SELECT * FROM customers WHERE user_id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();
                
                if (resultSet.next()) {
                    customer = new Customer();
                    customer.setId(resultSet.getInt("id"));
                    customer.setAccountNumber(resultSet.getString("account_number"));
                    customer.setUserId(resultSet.getInt("user_id"));
                    customer.setName(resultSet.getString("name"));
                    customer.setEmail(resultSet.getString("email"));
                    customer.setPhone(resultSet.getString("phone"));
                    customer.setAddress(resultSet.getString("address"));
                    customer.setBalance(resultSet.getDouble("balance"));
                }
                
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            session.setAttribute("customer", customer);
        }
        
        request.setAttribute("customer", customer);
        request.getRequestDispatcher("/user/updateProfile.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"user".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Get customer from session or database
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            Integer userId = (Integer) session.getAttribute("userId");
            try {
                Connection connection = DatabaseConnection.getConnection();
                String sql = "SELECT * FROM customers WHERE user_id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();
                
                if (resultSet.next()) {
                    customer = new Customer();
                    customer.setId(resultSet.getInt("id"));
                    customer.setAccountNumber(resultSet.getString("account_number"));
                    customer.setUserId(resultSet.getInt("user_id"));
                    customer.setName(resultSet.getString("name"));
                    customer.setEmail(resultSet.getString("email"));
                    customer.setPhone(resultSet.getString("phone"));
                    customer.setAddress(resultSet.getString("address"));
                    customer.setBalance(resultSet.getDouble("balance"));
                }
                
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Get form data
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        
        // Update customer details
        customer.setName(name != null ? name : customer.getName());
        customer.setPhone(phone != null ? phone : customer.getPhone());
        customer.setAddress(address != null ? address : customer.getAddress());
        
        // Update customer in database
        boolean updated = false;
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "UPDATE customers SET name = ?, email = ?, phone = ?, address = ? WHERE account_number = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getEmail());
            statement.setString(3, customer.getPhone());
            statement.setString(4, customer.getAddress());
            statement.setString(5, customer.getAccountNumber());
            
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                updated = true;
            }
            
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (updated) {
            // Update password if provided
            if (password != null && !password.trim().isEmpty()) {
                // Get user from database
                User user = null;
                try {
                    Connection connection = DatabaseConnection.getConnection();
                    String sql = "SELECT * FROM users WHERE email = ?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, customer.getEmail());
                    ResultSet resultSet = statement.executeQuery();
                    
                    if (resultSet.next()) {
                        user = new User();
                        user.setId(resultSet.getInt("id"));
                        user.setEmail(resultSet.getString("email"));
                        user.setPassword(resultSet.getString("password"));
                        user.setRole(resultSet.getString("role"));
                    }
                    
                    resultSet.close();
                    statement.close();
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                if (user != null) {
                    // Update password in database
                    try {
                        Connection connection = DatabaseConnection.getConnection();
                        String sql = "UPDATE users SET password = ? WHERE id = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, PasswordUtil.hashPassword(password));
                        statement.setInt(2, user.getId());
                        statement.executeUpdate();
                        statement.close();
                        connection.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            
            // Get updated customer from database
            Customer updatedCustomer = null;
            try {
                Connection connection = DatabaseConnection.getConnection();
                String sql = "SELECT * FROM customers WHERE account_number = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, customer.getAccountNumber());
                ResultSet resultSet = statement.executeQuery();
                
                if (resultSet.next()) {
                    updatedCustomer = new Customer();
                    updatedCustomer.setId(resultSet.getInt("id"));
                    updatedCustomer.setAccountNumber(resultSet.getString("account_number"));
                    updatedCustomer.setUserId(resultSet.getInt("user_id"));
                    updatedCustomer.setName(resultSet.getString("name"));
                    updatedCustomer.setEmail(resultSet.getString("email"));
                    updatedCustomer.setPhone(resultSet.getString("phone"));
                    updatedCustomer.setAddress(resultSet.getString("address"));
                    updatedCustomer.setBalance(resultSet.getDouble("balance"));
                }
                
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            session.setAttribute("customer", updatedCustomer);
            request.setAttribute("success", "Profile updated successfully!");
            request.setAttribute("customer", updatedCustomer);
        } else {
            request.setAttribute("error", "Failed to update profile");
            request.setAttribute("customer", customer);
        }
        
        request.getRequestDispatcher("/user/updateProfile.jsp").forward(request, response);
    }
}
