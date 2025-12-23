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

// This servlet handles when someone wants to create a new account
public class RegisterServlet extends HttpServlet {
    
    // This runs when someone visits the registration page
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if admin is logged in
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        // Show the registration page
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    // This runs when someone submits the registration form
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        try {
            Connection connection = DatabaseConnection.getConnection();
            
            // Add user
            String sqlUser = "INSERT INTO users (email, password, role) VALUES (?, ?, 'user')";
            PreparedStatement stmt = connection.prepareStatement(sqlUser, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            int userId = 0;
            if (rs.next()) userId = rs.getInt(1);
            
            // Add customer
            String sqlCust = "INSERT INTO customers (account_number, user_id, name, email, phone, address, balance) VALUES (?, ?, ?, ?, ?, ?, 0.0)";
            stmt = connection.prepareStatement(sqlCust);
            stmt.setString(1, "ACC" + System.currentTimeMillis());
            stmt.setInt(2, userId);
            stmt.setString(3, name);
            stmt.setString(4, email);
            stmt.setString(5, phone);
            stmt.setString(6, address);
            stmt.executeUpdate();
            
            connection.close();
            response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp?msg=registered");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/register.jsp?error=failed");
        }
    }
}
