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

// This servlet shows the user's dashboard with their account info and recent transactions
public class UserDashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || !"user".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Get customer account info from database
            connection = DatabaseConnection.getConnection();
            Integer userId = (Integer) session.getAttribute("userId");
            
            String customerSql = "SELECT * FROM customers WHERE user_id = ?";
            statement = connection.prepareStatement(customerSql);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                // Save customer info so we can show it on the page
                request.setAttribute("customerId", resultSet.getInt("id"));
                request.setAttribute("accountNumber", resultSet.getString("account_number"));
                request.setAttribute("customerName", resultSet.getString("name"));
                request.setAttribute("customerEmail", resultSet.getString("email"));
                request.setAttribute("customerPhone", resultSet.getString("phone"));
                request.setAttribute("customerAddress", resultSet.getString("address"));
                request.setAttribute("balance", resultSet.getDouble("balance"));
                
                String accountNumber = resultSet.getString("account_number");
                
                resultSet.close();
                statement.close();
                
                // Get all transactions for this account
                String transactionSql = "SELECT * FROM transactions WHERE sender_account_number = ? OR receiver_account_number = ? ORDER BY created_at DESC";
                statement = connection.prepareStatement(transactionSql);
                statement.setString(1, accountNumber);
                statement.setString(2, accountNumber);
                resultSet = statement.executeQuery();
                
                // Put all transactions in a list
                List<Map<String, Object>> allTransactions = new ArrayList<>();
                while (resultSet.next()) {
                    Map<String, Object> transaction = new HashMap<>();
                    transaction.put("id", resultSet.getInt("id"));
                    transaction.put("transactionType", resultSet.getString("transaction_type"));
                    transaction.put("senderAccountNumber", resultSet.getString("sender_account_number"));
                    transaction.put("receiverAccountNumber", resultSet.getString("receiver_account_number"));
                    transaction.put("amount", resultSet.getDouble("amount"));
                    transaction.put("note", resultSet.getString("note"));
                    transaction.put("createdAt", resultSet.getTimestamp("created_at"));
                    allTransactions.add(transaction);
                }
                
                // Get only the first 5 transactions to show on dashboard
                List<Map<String, Object>> recentTransactions = new ArrayList<>();
                int maxItems = 5;
                for (int i = 0; i < allTransactions.size() && i < maxItems; i++) {
                    recentTransactions.add(allTransactions.get(i));
                }
                
                request.setAttribute("recentTransactions", recentTransactions);
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

        // Show the dashboard page
        request.getRequestDispatcher("/user/dashboard.jsp").forward(request, response);
    }
}
