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

public class DeleteCustomerServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String accountNumber = request.getParameter("accountNumber");
        if (accountNumber != null) {
            // Delete customer from database
            boolean deleted = false;
            try {
                Connection connection = DatabaseConnection.getConnection();
                String sql = "DELETE FROM customers WHERE account_number = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, accountNumber);
                
                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    deleted = true;
                }
                
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            if (deleted) {
                request.setAttribute("success", "Customer deleted successfully!");
            } else {
                request.setAttribute("error", "Failed to delete customer");
            }
        } else {
            request.setAttribute("error", "Failed to delete customer");
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/viewCustomers.jsp");
    }
}
