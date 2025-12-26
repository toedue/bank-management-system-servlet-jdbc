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

/**
 * ADMIN DASHBOARD SERVLET
 * This servlet calculates and shows the total number of customers and transactions
 * on the main admin home page.
 */
public class AdminDashboardServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int totalCustomersCount = 0;
        int totalTransactionsCount = 0;
        
        // Connect to the database and count records
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement()) {
            
            // Step 1: Count how many customers we have
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM customers");
            if (rs.next()) {
                totalCustomersCount = rs.getInt(1);
            }
            
            // Step 2: Count how many transactions have been made
            rs = stmt.executeQuery("SELECT COUNT(*) FROM transactions");
            if (rs.next()) {
                totalTransactionsCount = rs.getInt(1);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Pass the counts to the JSP page
        request.setAttribute("totalCustomers", totalCustomersCount);
        request.setAttribute("totalTransactions", totalTransactionsCount);
        
        // Show the admin dashboard webpage
        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }
}
