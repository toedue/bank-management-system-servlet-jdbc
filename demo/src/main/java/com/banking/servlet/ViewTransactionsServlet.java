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
        
        HttpSession session = request.getSession(false);
        String accountNumber = (String) session.getAttribute("accountNumber");
        List<Map<String, Object>> transactions = new ArrayList<>();
        
        if (accountNumber != null) {
             try {
                Connection connection = DatabaseConnection.getConnection();
                String sql = "SELECT * FROM transactions WHERE sender_account_number = ? OR receiver_account_number = ? ORDER BY created_at DESC";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, accountNumber);
                statement.setString(2, accountNumber);
                ResultSet resultSet = statement.executeQuery();
                
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
                connection.close();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        request.setAttribute("transactions", transactions);
        request.getRequestDispatcher("/user/viewTransactions.jsp").forward(request, response);
    }
}
