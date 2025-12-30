package com.banking.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.banking.DB.DatabaseConnection;

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
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                HttpSession session = request.getSession();
                int userId = resultSet.getInt("id");
                String role = resultSet.getString("role");
                String userEmail = resultSet.getString("email");
                
                session.setAttribute("userId", userId);
                session.setAttribute("userRole", role);
                session.setAttribute("userEmail", userEmail);
                
                if ("admin".equals(role)) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                } else {
                    // Fetch additional customer info for user
                    String customerSql = "SELECT * FROM customers WHERE user_id = ?";
                    PreparedStatement customerStmt = connection.prepareStatement(customerSql);
                    customerStmt.setInt(1, userId);
                    ResultSet customerRs = customerStmt.executeQuery();
                    
                    if (customerRs.next()) {
                        session.setAttribute("accountNumber", customerRs.getString("account_number"));
                        session.setAttribute("customerName", customerRs.getString("name"));
                        session.setAttribute("balance", customerRs.getDouble("balance"));
                    }
                    response.sendRedirect(request.getContextPath() + "/user/dashboard");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=invalid");
            }
            connection.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=invalid");
        }
    }
}
