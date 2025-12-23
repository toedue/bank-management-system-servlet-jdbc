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
import java.util.HashMap;
import java.util.Map;

// This servlet lets admin edit customer information
public class EditCustomerServlet extends HttpServlet {
    
    // This runs when admin visits the edit customer page
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if admin is logged in
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String accountNumber = request.getParameter("accountNumber");
        if (accountNumber != null) {
            // Get customer from database
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            
            try {
                connection = DatabaseConnection.getConnection();
                String sql = "SELECT * FROM customers WHERE account_number = ?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, accountNumber);
                resultSet = statement.executeQuery();
                
                if (resultSet.next()) {
                    // Put customer info in a map
                    Map<String, Object> customer = new HashMap<>();
                    customer.put("id", resultSet.getInt("id"));
                    customer.put("accountNumber", resultSet.getString("account_number"));
                    customer.put("userId", resultSet.getInt("user_id"));
                    customer.put("name", resultSet.getString("name"));
                    customer.put("email", resultSet.getString("email"));
                    customer.put("phone", resultSet.getString("phone"));
                    customer.put("address", resultSet.getString("address"));
                    customer.put("balance", resultSet.getDouble("balance"));
                    request.setAttribute("customer", customer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (resultSet != null) resultSet.close();
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
    }
    
    // This runs when admin submits the edit customer form
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String accountNumber = request.getParameter("accountNumber");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "UPDATE customers SET name = ?, email = ?, phone = ?, address = ? WHERE account_number = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, phone);
            statement.setString(4, address);
            statement.setString(5, accountNumber);
            
            statement.executeUpdate();
            connection.close();
            
            response.sendRedirect(request.getContextPath() + "/admin/editCustomer.jsp?accountNumber="+accountNumber+"&msg=updated");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/editCustomer.jsp?accountNumber="+accountNumber+"&error=failed");
        }
    }
}
