package com.banking.servlet;

import com.banking.util.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

// This servlet shows the admin dashboard with total customers and transactions
public class AdminDashboardServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int totalCustomers = 0;
        int totalTransactions = 0;
        
        try {
            Connection connection = DatabaseConnection.getConnection();
            
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM customers");
            if (rs.next()) totalCustomers = rs.getInt(1);
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM transactions");
            if (rs.next()) totalTransactions = rs.getInt(1);
            
            connection.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        request.setAttribute("totalCustomers", totalCustomers);
        request.setAttribute("totalTransactions", totalTransactions);
        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }
}
