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

/**
 * EDIT CUSTOMER SERVLET (Admin Only)
 * Allows admins to change customer details like name, phone, etc.
 * 1. GET: Fetches customer details based on their account number.
 * 2. POST: Saves the new details into the database.
 */
public class EditCustomerServlet extends HttpServlet {
    
    // 1. FETCH CUSTOMER INFO TO SHOW IN FORM
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Safety Check: Only admins should be here
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String accountNumber = request.getParameter("accountNumber");
        if (accountNumber != null) {
            
            // Try-with-resources: automatically closes the connection when done
            try (Connection connection = DatabaseConnection.getConnection()) {
                String sql = "SELECT * FROM customers WHERE account_number = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, accountNumber);
                ResultSet resultSet = statement.executeQuery();
                
                if (resultSet.next()) {
                    // Pass each detail separately to make the JSP simpler
                    request.setAttribute("custName", resultSet.getString("name"));
                    request.setAttribute("custEmail", resultSet.getString("email"));
                    request.setAttribute("custPhone", resultSet.getString("phone"));
                    request.setAttribute("custAddress", resultSet.getString("address"));
                    request.setAttribute("custAccountNumber", resultSet.getString("account_number"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Show the edit customer page
        request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
    }
    
    // 2. SAVE THE EDITED CUSTOMER INFO
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get the data from the form
        String accountNumber = request.getParameter("accountNumber");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        
        try (Connection connection = DatabaseConnection.getConnection()) {
            // SQL command to update the customer record
            String sql = "UPDATE customers SET name = ?, email = ?, phone = ?, address = ? WHERE account_number = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, phone);
            statement.setString(4, address);
            statement.setString(5, accountNumber);
            
            statement.executeUpdate();
            
            // Redirect back with a success message
            response.sendRedirect(request.getContextPath() + "/admin/editCustomer.jsp?accountNumber=" + accountNumber + "&msg=updated");

        } catch (Exception e) {
            e.printStackTrace();
            // Redirect back with an error message
            response.sendRedirect(request.getContextPath() + "/admin/editCustomer.jsp?accountNumber=" + accountNumber + "&error=failed");
        }
    }
}
