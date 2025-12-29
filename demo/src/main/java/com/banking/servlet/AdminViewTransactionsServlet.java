package com.banking.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.banking.DB.DatabaseConnection;

// This servlet shows all transactions to the admin
public class AdminViewTransactionsServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if admin is logged in
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Get all transactions from database
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<Map<String, Object>> transactions = new ArrayList<>();
        
        try {
            connection = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM transactions ORDER BY created_at DESC";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            
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
        
        // Send transaction list to the page
        request.setAttribute("transactions", transactions);
        request.getRequestDispatcher("/admin/viewTransactions.jsp").forward(request, response);
    }
}
