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

        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");
        List<Map<String, Object>> recentTransactions = new ArrayList<>();

        if (userId != null) {
            try {
                Connection connection = DatabaseConnection.getConnection();
                
                // Get customer
                PreparedStatement stmt = connection.prepareStatement("SELECT * FROM customers WHERE user_id = ?");
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    request.setAttribute("customerId", rs.getInt("id"));
                    request.setAttribute("accountNumber", rs.getString("account_number"));
                    request.setAttribute("customerName", rs.getString("name"));
                    request.setAttribute("customerEmail", rs.getString("email"));
                    request.setAttribute("customerPhone", rs.getString("phone"));
                    request.setAttribute("customerAddress", rs.getString("address"));
                    request.setAttribute("balance", rs.getDouble("balance"));
                    
                    String accNum = rs.getString("account_number");
                    
                    // Get recent transactions
                    stmt = connection.prepareStatement("SELECT * FROM transactions WHERE sender_account_number = ? OR receiver_account_number = ? ORDER BY created_at DESC LIMIT 5");
                    stmt.setString(1, accNum);
                    stmt.setString(2, accNum);
                    rs = stmt.executeQuery();
                    
                    while (rs.next()) {
                        Map<String, Object> transaction = new HashMap<>();
                        transaction.put("id", rs.getInt("id"));
                        transaction.put("transactionType", rs.getString("transaction_type"));
                        transaction.put("senderAccountNumber", rs.getString("sender_account_number"));
                        transaction.put("receiverAccountNumber", rs.getString("receiver_account_number"));
                        transaction.put("amount", rs.getDouble("amount"));
                        transaction.put("note", rs.getString("note"));
                        transaction.put("createdAt", rs.getTimestamp("created_at"));
                        recentTransactions.add(transaction);
                    }
                }
                connection.close();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        request.setAttribute("recentTransactions", recentTransactions);
        request.getRequestDispatcher("/user/dashboard.jsp").forward(request, response);
    }
}
