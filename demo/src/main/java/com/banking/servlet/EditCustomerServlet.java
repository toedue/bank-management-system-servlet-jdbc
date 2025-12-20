package com.banking.servlet;

import com.banking.model.Customer;
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

public class EditCustomerServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String accountNumber = request.getParameter("accountNumber");
        if (accountNumber != null) {
            // Get customer from database
            Customer customer = null;
            try {
                Connection connection = DatabaseConnection.getConnection();
                String sql = "SELECT * FROM customers WHERE account_number = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, accountNumber);
                ResultSet resultSet = statement.executeQuery();
                
                if (resultSet.next()) {
                    customer = new Customer();
                    customer.setId(resultSet.getInt("id"));
                    customer.setAccountNumber(resultSet.getString("account_number"));
                    customer.setUserId(resultSet.getInt("user_id"));
                    customer.setName(resultSet.getString("name"));
                    customer.setEmail(resultSet.getString("email"));
                    customer.setPhone(resultSet.getString("phone"));
                    customer.setAddress(resultSet.getString("address"));
                    customer.setBalance(resultSet.getDouble("balance"));
                }
                
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            request.setAttribute("customer", customer);
        }
        request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String accountNumber = request.getParameter("accountNumber");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        
        if (accountNumber == null || name == null || email == null) {
            request.setAttribute("error", "Invalid data");
            request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
            return;
        }
        
        // Update customer in database
        boolean updated = false;
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "UPDATE customers SET name = ?, email = ?, phone = ?, address = ? WHERE account_number = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, phone != null ? phone : "");
            statement.setString(4, address != null ? address : "");
            statement.setString(5, accountNumber);
            
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                updated = true;
            }
            
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (updated) {
            // Get updated customer from database
            Customer customer = null;
            try {
                Connection connection = DatabaseConnection.getConnection();
                String sql = "SELECT * FROM customers WHERE account_number = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, accountNumber);
                ResultSet resultSet = statement.executeQuery();
                
                if (resultSet.next()) {
                    customer = new Customer();
                    customer.setId(resultSet.getInt("id"));
                    customer.setAccountNumber(resultSet.getString("account_number"));
                    customer.setUserId(resultSet.getInt("user_id"));
                    customer.setName(resultSet.getString("name"));
                    customer.setEmail(resultSet.getString("email"));
                    customer.setPhone(resultSet.getString("phone"));
                    customer.setAddress(resultSet.getString("address"));
                    customer.setBalance(resultSet.getDouble("balance"));
                }
                
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            request.setAttribute("success", "Customer updated successfully!");
            request.setAttribute("customer", customer);
        } else {
            Customer customer = new Customer();
            customer.setAccountNumber(accountNumber);
            customer.setName(name);
            customer.setEmail(email);
            customer.setPhone(phone != null ? phone : "");
            customer.setAddress(address != null ? address : "");
            request.setAttribute("error", "Failed to update customer");
            request.setAttribute("customer", customer);
        }
        request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
    }
}
