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
                session.setAttribute("userId", resultSet.getInt("id"));
                session.setAttribute("userRole", resultSet.getString("role"));
                
                if ("admin".equals(resultSet.getString("role"))) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
                } else {
                    response.sendRedirect(request.getContextPath() + "/user/dashboard.jsp");
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
