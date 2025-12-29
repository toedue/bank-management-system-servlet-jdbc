package com.banking.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.banking.DB.DatabaseConnection;

// This servlet lets admin delete a customer
public class DeleteCustomerServlet extends HttpServlet {
    
    // This runs when admin wants to delete a customer
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String accountNumber = request.getParameter("accountNumber");
        
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "DELETE FROM customers WHERE account_number = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, accountNumber);
            statement.executeUpdate();
            
            connection.close();
            response.sendRedirect(request.getContextPath() + "/admin/viewCustomers.jsp?msg=deleted");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/viewCustomers.jsp?error=failed");
        }
    }
}
