package com.banking.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.banking.DB.DatabaseConnection;


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
        
        String transactionType = request.getParameter("transactionType");
        String accountNumber = request.getParameter("accountNumber");
        String receiverAccountNumber = request.getParameter("receiverAccountNumber");
        double amount = Double.parseDouble(request.getParameter("amount"));
        String note = request.getParameter("note");
        
        try {
            Connection connection = DatabaseConnection.getConnection();
            
            if ("deposit".equals(transactionType)) {
                String sql = "UPDATE customers SET balance = balance + ? WHERE account_number = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setDouble(1, amount);
                stmt.setString(2, accountNumber);
                stmt.executeUpdate();
                
                String txnSql = "INSERT INTO transactions (transaction_type, sender_account_number, amount, note) VALUES ('deposit', ?, ?, ?)";
                stmt = connection.prepareStatement(txnSql);
                stmt.setString(1, accountNumber);
                stmt.setDouble(2, amount);
                stmt.setString(3, note);
                stmt.executeUpdate();
                
            } else if ("withdrawal".equals(transactionType)) {
                String sql = "UPDATE customers SET balance = balance - ? WHERE account_number = ? AND balance >= ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setDouble(1, amount);
                stmt.setString(2, accountNumber);
                stmt.setDouble(3, amount);
                int rows = stmt.executeUpdate();
                
                if (rows > 0) {
                    String txnSql = "INSERT INTO transactions (transaction_type, sender_account_number, amount, note) VALUES ('withdrawal', ?, ?, ?)";
                    stmt = connection.prepareStatement(txnSql);
                    stmt.setString(1, accountNumber);
                    stmt.setDouble(2, amount);
                    stmt.setString(3, note);
                    stmt.executeUpdate();
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/manageTransaction.jsp?error=insufficient_funds");
                    connection.close();
                    return;
                }

            } else if ("transfer".equals(transactionType)) {
                 // Debiting sender
                String sql = "UPDATE customers SET balance = balance - ? WHERE account_number = ? AND balance >= ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setDouble(1, amount);
                stmt.setString(2, accountNumber);
                stmt.setDouble(3, amount);
                int rows = stmt.executeUpdate();
                
                if (rows > 0) {
                    // Crediting receiver
                    String creditSql = "UPDATE customers SET balance = balance + ? WHERE account_number = ?";
                    stmt = connection.prepareStatement(creditSql);
                    stmt.setDouble(1, amount);
                    stmt.setString(2, receiverAccountNumber);
                    stmt.executeUpdate();
                    
                    String txnSql = "INSERT INTO transactions (transaction_type, sender_account_number, receiver_account_number, amount, note) VALUES ('transfer', ?, ?, ?, ?)";
                    stmt = connection.prepareStatement(txnSql);
                    stmt.setString(1, accountNumber);
                    stmt.setString(2, receiverAccountNumber);
                    stmt.setDouble(3, amount);
                    stmt.setString(4, note);
                    stmt.executeUpdate();
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/manageTransaction.jsp?error=insufficient_funds");
                    connection.close();
                    return;
                }
            }
            
            connection.close();
            response.sendRedirect(request.getContextPath() + "/admin/manageTransaction.jsp?msg=success");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/manageTransaction.jsp?error=failed");
        }
    }
}
