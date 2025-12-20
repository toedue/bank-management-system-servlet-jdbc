package com.banking.servlet;

import com.banking.model.Transaction;
import com.banking.util.DatabaseConnection;

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
import java.util.List;

public class AdminViewTransactionsServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Get all transactions from database
        List<Transaction> transactions = new ArrayList<>();
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM transactions ORDER BY created_at DESC";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            
            while (resultSet.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(resultSet.getInt("id"));
                transaction.setTransactionType(resultSet.getString("transaction_type"));
                transaction.setSenderAccountNumber(resultSet.getString("sender_account_number"));
                transaction.setReceiverAccountNumber(resultSet.getString("receiver_account_number"));
                transaction.setAmount(resultSet.getDouble("amount"));
                transaction.setNote(resultSet.getString("note"));
                transaction.setCreatedAt(resultSet.getTimestamp("created_at"));
                transactions.add(transaction);
            }
            
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        request.setAttribute("transactions", transactions);
        request.getRequestDispatcher("/admin/viewTransactions.jsp").forward(request, response);
    }
}
