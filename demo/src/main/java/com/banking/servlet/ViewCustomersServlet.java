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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This servlet shows all customers to the admin
public class ViewCustomersServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Map<String, Object>> customers = new ArrayList<>();
        
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM customers ORDER BY created_at DESC";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            
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
            
            connection.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        request.setAttribute("customers", customers);
        request.getRequestDispatcher("/admin/viewCustomers.jsp").forward(request, response);
    }
}
