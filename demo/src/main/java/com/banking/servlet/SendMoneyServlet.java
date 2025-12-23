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
        String senderAccountNumber = (String) session.getAttribute("accountNumber");
        
        String receiverAccountNumber = request.getParameter("receiverAccountNumber");
        double amount = Double.parseDouble(request.getParameter("amount"));
        String note = request.getParameter("note");
        
        try {
            Connection connection = DatabaseConnection.getConnection();
            
            // Check sender balance and deduct
            String debitSql = "UPDATE customers SET balance = balance - ? WHERE account_number = ? AND balance >= ?";
            PreparedStatement stmt = connection.prepareStatement(debitSql);
            stmt.setDouble(1, amount);
            stmt.setString(2, senderAccountNumber);
            stmt.setDouble(3, amount);
            int rows = stmt.executeUpdate();
            
            if (rows > 0) {
                 // Add money to receiver
                String creditSql = "UPDATE customers SET balance = balance + ? WHERE account_number = ?";
                stmt = connection.prepareStatement(creditSql);
                stmt.setDouble(1, amount);
                stmt.setString(2, receiverAccountNumber);
                stmt.executeUpdate();
                
                 // Record transaction
                String txnSql = "INSERT INTO transactions (transaction_type, sender_account_number, receiver_account_number, amount, note) VALUES ('transfer', ?, ?, ?, ?)";
                stmt = connection.prepareStatement(txnSql);
                stmt.setString(1, senderAccountNumber);
                stmt.setString(2, receiverAccountNumber);
                stmt.setDouble(3, amount);
                stmt.setString(4, note);
                stmt.executeUpdate();
                
                // Update session balance
                String balSql = "SELECT balance FROM customers WHERE account_number = ?";
                stmt = connection.prepareStatement(balSql);
                stmt.setString(1, senderAccountNumber);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    session.setAttribute("balance", rs.getDouble("balance"));
                }
                
                response.sendRedirect(request.getContextPath() + "/user/sendMoney.jsp?msg=success");
            } else {
                 response.sendRedirect(request.getContextPath() + "/user/sendMoney.jsp?error=insufficient_funds");
            }
            connection.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/user/sendMoney.jsp?error=failed");
        }
    }
}
