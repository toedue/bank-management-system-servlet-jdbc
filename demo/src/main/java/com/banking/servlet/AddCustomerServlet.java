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

public class AddCustomerServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        request.getRequestDispatcher("/admin/addCustomer.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Get form data
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String balanceStr = request.getParameter("balance");
        
        // Check if required fields are filled
        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Please fill all required fields");
            request.getRequestDispatcher("/admin/addCustomer.jsp").forward(request, response);
            return;
        }
        
        // Check if email already exists
        boolean emailExists = false;
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    emailExists = true;
                }
            }
            
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (emailExists) {
            request.setAttribute("error", "Email already registered");
            request.getRequestDispatcher("/admin/addCustomer.jsp").forward(request, response);
            return;
        }
        
        // Convert balance string to number
        double balance = 0.0;
        try {
            balance = Double.parseDouble(balanceStr != null ? balanceStr : "0");
        } catch (NumberFormatException e) {
            balance = 0.0;
        }
        
        // Insert new user into database
        boolean userInserted = false;
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "INSERT INTO users (email, password, role) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            statement.setString(2, PasswordUtil.hashPassword(password));
            statement.setString(3, "user");
            
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                userInserted = true;
            }
            
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (!userInserted) {
            request.setAttribute("error", "Failed to add customer");
            request.getRequestDispatcher("/admin/addCustomer.jsp").forward(request, response);
            return;
        }
        
        // Get the newly created user from database
        User newUser = null;
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM users WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                if (PasswordUtil.verifyPassword(password, storedPassword)) {
                    newUser = new User();
                    newUser.setId(resultSet.getInt("id"));
                    newUser.setEmail(resultSet.getString("email"));
                    newUser.setPassword(resultSet.getString("password"));
                    newUser.setRole(resultSet.getString("role"));
                }
            }
            
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (newUser == null) {
            request.setAttribute("error", "Failed to add customer");
            request.getRequestDispatcher("/admin/addCustomer.jsp").forward(request, response);
            return;
        }
        
        // Insert customer into database
        boolean customerInserted = false;
        try {
            Connection connection = DatabaseConnection.getConnection();
            String accountNumber = "ACC" + System.currentTimeMillis();
            String sql = "INSERT INTO customers (account_number, user_id, name, email, phone, address, balance) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, accountNumber);
            statement.setInt(2, newUser.getId());
            statement.setString(3, name);
            statement.setString(4, email);
            statement.setString(5, phone != null ? phone : "");
            statement.setString(6, address != null ? address : "");
            statement.setDouble(7, balance);
            
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                customerInserted = true;
            }
            
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (customerInserted) {
            request.setAttribute("success", "Customer added successfully!");
        } else {
            request.setAttribute("error", "Failed to add customer");
        }
        request.getRequestDispatcher("/admin/addCustomer.jsp").forward(request, response);
    }
}
