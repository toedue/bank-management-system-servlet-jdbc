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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This servlet shows all transactions for the logged-in user
public class ViewTransactionsServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || !"user".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Get account number from session
        String accountNumber = (String) session.getAttribute("accountNumber");
        if (accountNumber == null) {
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
                    accountNumber = resultSet.getString("account_number");
                    session.setAttribute("accountNumber", accountNumber);
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
        
        if (accountNumber != null) {
            // Get all transactions for this account
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            List<Map<String, Object>> transactions = new ArrayList<>();
            
            try {
                connection = DatabaseConnection.getConnection();
                String sql = "SELECT * FROM transactions WHERE sender_account_number = ? OR receiver_account_number = ? ORDER BY created_at DESC";
                statement = connection.prepareStatement(sql);
                statement.setString(1, accountNumber);
                statement.setString(2, accountNumber);
                resultSet = statement.executeQuery();
                
                // Read each transaction and put it in a list
                while (resultSet.next()) {
                    Map<String, Object> transaction = new HashMap<>();
                    transaction.put("id", resultSet.getInt("id"));
                    transaction.put("transactionType", resultSet.getString("transaction_type"));
                    transaction.put("senderAccountNumber", resultSet.getString("sender_account_number"));
                    transaction.put("receiverAccountNumber", resultSet.getString("receiver_account_number"));
                    transaction.put("amount", resultSet.getDouble("amount"));
                    transaction.put("note", resultSet.getString("note"));
                    transaction.put("createdAt", resultSet.getTimestamp("created_at"));
                    transactions.add(transaction);
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
            
            request.setAttribute("transactions", transactions);
        }
        
        request.getRequestDispatcher("/user/viewTransactions.jsp").forward(request, response);
    }
}
