package com.banking.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.banking.DB.DatabaseConnection;

/**
 * UPDATE PROFILE SERVLET
 * This servlet handles two things:
 * 1. GET: Fetches the user's current info to show on the update form.
 * 2. POST: Takes the new info from the form and saves it to the database.
 */
public class UpdateProfileServlet extends HttpServlet {
    
    // 1. SHOW THE UPDATE FORM
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");
        
        // Connect to database and fetch current details
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM customers WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                // Pass individual values to the JSP (simpler than using a Map)
                request.setAttribute("customerName", resultSet.getString("name"));
                request.setAttribute("customerEmail", resultSet.getString("email"));
                request.setAttribute("customerPhone", resultSet.getString("phone"));
                request.setAttribute("customerAddress", resultSet.getString("address"));
                request.setAttribute("accountNumber", resultSet.getString("account_number"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Send the user to the update profile webpage
        request.getRequestDispatcher("/user/updateProfile.jsp").forward(request, response);
    }
    
    // 2. SAVE THE UPDATED INFO
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        String accountNumber = (String) session.getAttribute("accountNumber");
        String userEmail = (String) session.getAttribute("userEmail");
        
        // Get the new data from the form fields
        String newName = request.getParameter("name");
        String newPhone = request.getParameter("phone");
        String newAddress = request.getParameter("address");
        String newPassword = request.getParameter("password");
        
        try (Connection connection = DatabaseConnection.getConnection()) {
            
            // Step A: Update the Customer table
            String customerSql = "UPDATE customers SET name = ?, phone = ?, address = ? WHERE account_number = ?";
            PreparedStatement customerStmt = connection.prepareStatement(customerSql);
            customerStmt.setString(1, newName);
            customerStmt.setString(2, newPhone);
            customerStmt.setString(3, newAddress);
            customerStmt.setString(4, accountNumber);
            customerStmt.executeUpdate();
            
            // Step B: Update the User table (for the password) if the user typed a new one
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                String userSql = "UPDATE users SET password = ? WHERE email = ?";
                PreparedStatement userStmt = connection.prepareStatement(userSql);
                userStmt.setString(1, newPassword);
                userStmt.setString(2, userEmail);
                userStmt.executeUpdate();
            }
            
            // Success! Send back to the page with a success message
            response.sendRedirect(request.getContextPath() + "/user/updateProfile.jsp?msg=updated");
            
        } catch (Exception e) {
            e.printStackTrace();
            // Error! Send back to the page with an error message
            response.sendRedirect(request.getContextPath() + "/user/updateProfile.jsp?error=failed");
        }
    }
}
