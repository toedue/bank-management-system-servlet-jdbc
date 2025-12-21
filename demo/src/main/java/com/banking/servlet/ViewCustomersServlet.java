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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This servlet shows all customers to the admin
public class ViewCustomersServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if admin is logged in
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Get all customers from database
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<Map<String, Object>> customers = new ArrayList<>();
        
        try {
            connection = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM customers ORDER BY created_at DESC";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            
            // Read each customer and put it in a list
            while (resultSet.next()) {
                Map<String, Object> customer = new HashMap<>();
                customer.put("id", resultSet.getInt("id"));
                customer.put("accountNumber", resultSet.getString("account_number"));
                customer.put("userId", resultSet.getInt("user_id"));
                customer.put("name", resultSet.getString("name"));
                customer.put("email", resultSet.getString("email"));
                customer.put("phone", resultSet.getString("phone"));
                customer.put("address", resultSet.getString("address"));
                customer.put("balance", resultSet.getDouble("balance"));
                customers.add(customer);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Always close database connection
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Send customer list to the page
        request.setAttribute("customers", customers);
        request.getRequestDispatcher("/admin/viewCustomers.jsp").forward(request, response);
    }
}
