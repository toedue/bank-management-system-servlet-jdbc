package com.banking.servlet;

import com.banking.util.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class AdminDashboardServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Count total customers
        int totalCustomers = 0;
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM customers";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            
            if (resultSet.next()) {
                totalCustomers = resultSet.getInt(1);
            }
            
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Count total transactions
        int totalTransactions = 0;
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM transactions";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            
            if (resultSet.next()) {
                totalTransactions = resultSet.getInt(1);
            }
            
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        request.setAttribute("totalCustomers", totalCustomers);
        request.setAttribute("totalTransactions", totalTransactions);
        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }
}
