package com.banking.servlet;

import com.banking.model.Customer;
import com.banking.model.User;
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

public class RegisterServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get all form data
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        
        // Check if required fields are filled
        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Please fill all required fields");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        
        // Check if email already exists in database
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
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
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
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
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
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        
        // Create customer record
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
            statement.setDouble(7, 0.0);
            
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
            request.setAttribute("success", "Registration successful! Please login.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}

