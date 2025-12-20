package com.banking.servlet;

import com.banking.model.User;
import com.banking.model.Customer;
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

public class LoginServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get email and password from the form
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // Check if email and password are not empty
        if (email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Please enter email and password");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        
        // Try to find the user in the database
        User user = null;
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM users WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                
                // Check if password matches
                if (PasswordUtil.verifyPassword(password, storedPassword)) {
                    user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPassword(resultSet.getString("password"));
                    user.setRole(resultSet.getString("role"));
                }
            }
            
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // If user found, create session and redirect
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole());
            
            // If user is a normal user (not admin), get their customer info
            if ("user".equals(user.getRole())) {
                Customer customer = null;
                try {
                    Connection connection = DatabaseConnection.getConnection();
                    String sql = "SELECT * FROM customers WHERE user_id = ?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, user.getId());
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
            
            // Redirect based on role
            if ("admin".equals(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
            } else {
                response.sendRedirect(request.getContextPath() + "/user/dashboard.jsp");
            }
        } else {
            request.setAttribute("error", "Invalid email or password");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}

