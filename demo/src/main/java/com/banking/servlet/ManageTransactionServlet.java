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

// This servlet lets admin manage transactions (deposit, withdrawal, transfer)
public class ManageTransactionServlet extends HttpServlet {
    
    // This runs when admin visits the manage transaction page
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if admin is logged in
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        request.getRequestDispatcher("/admin/manageTransaction.jsp").forward(request, response);
    }
    
    // This runs when admin submits the transaction form
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if admin is logged in
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Get information from the form
        String transactionType = request.getParameter("transactionType");
        String accountNumber = request.getParameter("accountNumber");
        String receiverAccountNumber = request.getParameter("receiverAccountNumber");
        String amountStr = request.getParameter("amount");
        String note = request.getParameter("note");
        
        // Check if required fields are filled
        if (transactionType == null || accountNumber == null || amountStr == null) {
            request.setAttribute("error", "Please fill all required fields");
            request.getRequestDispatcher("/admin/manageTransaction.jsp").forward(request, response);
            return;
        }
        
        // Convert amount from string to number
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                request.setAttribute("error", "Amount must be greater than 0");
                request.getRequestDispatcher("/admin/manageTransaction.jsp").forward(request, response);
                return;
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid amount");
            request.getRequestDispatcher("/admin/manageTransaction.jsp").forward(request, response);
            return;
        }
        
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseConnection.getConnection();
            
            // Check if account exists
            String checkAccountSql = "SELECT COUNT(*) FROM customers WHERE account_number = ?";
            statement = connection.prepareStatement(checkAccountSql);
            statement.setString(1, accountNumber);
            resultSet = statement.executeQuery();
            
            boolean accountExists = false;
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    accountExists = true;
                }
            }
            
            resultSet.close();
            statement.close();
            
            if (!accountExists) {
                request.setAttribute("error", "Account not found");
                request.getRequestDispatcher("/admin/manageTransaction.jsp").forward(request, response);
                return;
            }
            
            // Process transaction based on type
            if ("deposit".equals(transactionType)) {
                // Add transaction to database
                String insertTransactionSql = "INSERT INTO transactions (transaction_type, sender_account_number, receiver_account_number, amount, note) VALUES (?, ?, ?, ?, ?)";
                statement = connection.prepareStatement(insertTransactionSql);
                statement.setString(1, "deposit");
                statement.setString(2, accountNumber);
                statement.setString(3, null);
                statement.setDouble(4, amount);
                statement.setString(5, note != null ? note : "");
                
                int rowsInserted = statement.executeUpdate();
                statement.close();
                
                if (rowsInserted > 0) {
                    // Add money to account
                    String updateBalanceSql = "UPDATE customers SET balance = balance + ? WHERE account_number = ?";
                    statement = connection.prepareStatement(updateBalanceSql);
                    statement.setDouble(1, amount);
                    statement.setString(2, accountNumber);
                    statement.executeUpdate();
                    statement.close();
                    request.setAttribute("success", "Deposit successful!");
                } else {
                    request.setAttribute("error", "Transaction failed");
                }
                
            } else if ("withdrawal".equals(transactionType)) {
                // Get customer balance
                String getBalanceSql = "SELECT balance FROM customers WHERE account_number = ?";
                statement = connection.prepareStatement(getBalanceSql);
                statement.setString(1, accountNumber);
                resultSet = statement.executeQuery();
                
                double balance = 0.0;
                if (resultSet.next()) {
                    balance = resultSet.getDouble("balance");
                }
                
                resultSet.close();
                statement.close();
                
                // Check if customer has enough money
                if (balance >= amount) {
                    // Add transaction to database
                    String insertTransactionSql = "INSERT INTO transactions (transaction_type, sender_account_number, receiver_account_number, amount, note) VALUES (?, ?, ?, ?, ?)";
                    statement = connection.prepareStatement(insertTransactionSql);
                    statement.setString(1, "withdrawal");
                    statement.setString(2, accountNumber);
                    statement.setString(3, null);
                    statement.setDouble(4, amount);
                    statement.setString(5, note != null ? note : "");
                    
                    int rowsInserted = statement.executeUpdate();
                    statement.close();
                    
                    if (rowsInserted > 0) {
                        // Take money from account
                        String updateBalanceSql = "UPDATE customers SET balance = balance - ? WHERE account_number = ?";
                        statement = connection.prepareStatement(updateBalanceSql);
                        statement.setDouble(1, amount);
                        statement.setString(2, accountNumber);
                        statement.executeUpdate();
                        statement.close();
                        request.setAttribute("success", "Withdrawal successful!");
                    } else {
                        request.setAttribute("error", "Transaction failed");
                    }
                } else {
                    request.setAttribute("error", "Insufficient balance");
                }
                
            } else if ("transfer".equals(transactionType)) {
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
                    request.getRequestDispatcher("/admin/manageTransaction.jsp").forward(request, response);
                    return;
                }
                
                // Get sender balance
                String getBalanceSql = "SELECT balance FROM customers WHERE account_number = ?";
                statement = connection.prepareStatement(getBalanceSql);
                statement.setString(1, accountNumber);
                resultSet = statement.executeQuery();
                
                double senderBalance = 0.0;
                if (resultSet.next()) {
                    senderBalance = resultSet.getDouble("balance");
                }
                
                resultSet.close();
                statement.close();
                
                // Check if sender has enough money
                if (senderBalance >= amount) {
                    // Add transaction to database
                    String insertTransactionSql = "INSERT INTO transactions (transaction_type, sender_account_number, receiver_account_number, amount, note) VALUES (?, ?, ?, ?, ?)";
                    statement = connection.prepareStatement(insertTransactionSql);
                    statement.setString(1, "transfer");
                    statement.setString(2, accountNumber);
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
                        statement.setString(2, accountNumber);
                        statement.executeUpdate();
                        statement.close();
                        
                        // Give money to receiver
                        String updateReceiverSql = "UPDATE customers SET balance = balance + ? WHERE account_number = ?";
                        statement = connection.prepareStatement(updateReceiverSql);
                        statement.setDouble(1, amount);
                        statement.setString(2, receiverAccountNumber);
                        statement.executeUpdate();
                        statement.close();
                        request.setAttribute("success", "Transfer successful!");
                    } else {
                        request.setAttribute("error", "Transaction failed");
                    }
                } else {
                    request.setAttribute("error", "Insufficient balance");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Transaction failed");
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
        
        request.getRequestDispatcher("/admin/manageTransaction.jsp").forward(request, response);
    }
}
