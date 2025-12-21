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

// This servlet handles when a user wants to send money to another account
public class SendMoneyServlet extends HttpServlet {
    
    // This runs when someone visits the send money page
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || !"user".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        // Show the send money page
        request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
    }
    
    // This runs when someone submits the send money form
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"user".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Get the information from the form
        String receiverAccountNumber = request.getParameter("receiverAccountNumber");
        String amountStr = request.getParameter("amount");
        String note = request.getParameter("note");
        
        // Check if receiver account and amount are filled
        if (receiverAccountNumber == null || receiverAccountNumber.trim().isEmpty() || 
            amountStr == null || amountStr.trim().isEmpty()) {
            request.setAttribute("error", "Please fill all required fields");
            request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
            return;
        }
        
        // Convert amount from string to number
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                request.setAttribute("error", "Amount must be greater than 0");
                request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
                return;
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid amount");
            request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
            return;
        }
        
        // Get sender account number from session
        String senderAccountNumber = (String) session.getAttribute("accountNumber");
        if (senderAccountNumber == null) {
            // Get from database if not in session
            Integer userId = (Integer) session.getAttribute("userId");
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                connection = DatabaseConnection.getConnection();
                String sql = "SELECT account_number FROM customers WHERE user_id = ?";
                statement = connection.prepareStatement(sql);
                statement.setInt(1, userId);
                resultSet = statement.executeQuery();
                
                if (resultSet.next()) {
                    senderAccountNumber = resultSet.getString("account_number");
                    session.setAttribute("accountNumber", senderAccountNumber);
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
        
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseConnection.getConnection();
            
            // Check if receiver account exists
            String checkReceiverSql = "SELECT COUNT(*) FROM customers WHERE account_number = ?";
            statement = connection.prepareStatement(checkReceiverSql);
            statement.setString(1, receiverAccountNumber);
            resultSet = statement.executeQuery();
            
            boolean receiverExists = false;
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    receiverExists = true;
                }
            }
            
            resultSet.close();
            statement.close();
            
            if (!receiverExists) {
                request.setAttribute("error", "Receiver account not found");
                request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
                return;
            }
            
            // Check if sender has enough money
            String checkBalanceSql = "SELECT balance FROM customers WHERE account_number = ?";
            statement = connection.prepareStatement(checkBalanceSql);
            statement.setString(1, senderAccountNumber);
            resultSet = statement.executeQuery();
            
            double senderBalance = 0.0;
            if (resultSet.next()) {
                senderBalance = resultSet.getDouble("balance");
            }
            
            resultSet.close();
            statement.close();
            
            if (senderBalance < amount) {
                request.setAttribute("error", "Insufficient balance");
                request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
                return;
            }
            
            // Add transaction to database
            String insertTransactionSql = "INSERT INTO transactions (transaction_type, sender_account_number, receiver_account_number, amount, note) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(insertTransactionSql);
            statement.setString(1, "transfer");
            statement.setString(2, senderAccountNumber);
            statement.setString(3, receiverAccountNumber);
            statement.setDouble(4, amount);
            statement.setString(5, note != null ? note : "");
            
            int rowsInserted = statement.executeUpdate();
            statement.close();
            
            if (rowsInserted > 0) {
                // Take money from sender
                String updateSenderSql = "UPDATE customers SET balance = balance - ? WHERE account_number = ?";
                statement = connection.prepareStatement(updateSenderSql);
                statement.setDouble(1, amount);
                statement.setString(2, senderAccountNumber);
                statement.executeUpdate();
                statement.close();
                
                // Give money to receiver
                String updateReceiverSql = "UPDATE customers SET balance = balance + ? WHERE account_number = ?";
                statement = connection.prepareStatement(updateReceiverSql);
                statement.setDouble(1, amount);
                statement.setString(2, receiverAccountNumber);
                statement.executeUpdate();
                statement.close();
                
                // Get updated balance for session
                String getUpdatedBalanceSql = "SELECT balance FROM customers WHERE account_number = ?";
                statement = connection.prepareStatement(getUpdatedBalanceSql);
                statement.setString(1, senderAccountNumber);
                resultSet = statement.executeQuery();
                
                if (resultSet.next()) {
                    double newBalance = resultSet.getDouble("balance");
                    session.setAttribute("balance", newBalance);
                }
                
                request.setAttribute("success", "Money sent successfully!");
            } else {
                request.setAttribute("error", "Transaction failed");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Transaction failed. Please try again.");
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
        
        request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
    }
}
