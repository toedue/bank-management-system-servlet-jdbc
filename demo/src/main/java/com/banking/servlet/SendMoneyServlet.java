package com.banking.servlet;

import com.banking.model.Customer;
import com.banking.model.Transaction;
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
import java.util.List;

public class SendMoneyServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"user".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"user".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Get sender customer from session
        Customer sender = (Customer) session.getAttribute("customer");
        if (sender == null) {
            // Get customer from database using user id
            Integer userId = (Integer) session.getAttribute("userId");
            try {
                Connection connection = DatabaseConnection.getConnection();
                String sql = "SELECT * FROM customers WHERE user_id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();
                
                if (resultSet.next()) {
                    sender = new Customer();
                    sender.setId(resultSet.getInt("id"));
                    sender.setAccountNumber(resultSet.getString("account_number"));
                    sender.setUserId(resultSet.getInt("user_id"));
                    sender.setName(resultSet.getString("name"));
                    sender.setEmail(resultSet.getString("email"));
                    sender.setPhone(resultSet.getString("phone"));
                    sender.setAddress(resultSet.getString("address"));
                    sender.setBalance(resultSet.getDouble("balance"));
                }
                
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            session.setAttribute("customer", sender);
        }
        
        // Get form data
        String receiverAccountNumber = request.getParameter("receiverAccountNumber");
        String amountStr = request.getParameter("amount");
        String note = request.getParameter("note");
        
        // Check if required fields are filled
        if (receiverAccountNumber == null || receiverAccountNumber.trim().isEmpty() ||
            amountStr == null || amountStr.trim().isEmpty()) {
            request.setAttribute("error", "Please fill all required fields");
            request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
            return;
        }
        
        // Convert amount string to number
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
        
        // Check if receiver account exists
        boolean receiverExists = false;
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM customers WHERE account_number = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, receiverAccountNumber);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    receiverExists = true;
                }
            }
            
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (!receiverExists) {
            request.setAttribute("error", "Receiver account not found");
            request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
            return;
        }
        
        // Check if sender has enough money
        if (sender.getBalance() < amount) {
            request.setAttribute("error", "Insufficient balance");
            request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
            return;
        }
        
        // Insert transaction into database
        boolean transactionInserted = false;
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "INSERT INTO transactions (transaction_type, sender_account_number, receiver_account_number, amount, note) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "transfer");
            statement.setString(2, sender.getAccountNumber());
            statement.setString(3, receiverAccountNumber);
            statement.setDouble(4, amount);
            statement.setString(5, note != null ? note : "");
            
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                transactionInserted = true;
            }
            
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (transactionInserted) {
            // Update sender balance (subtract amount)
            try {
                Connection connection = DatabaseConnection.getConnection();
                String sql = "UPDATE customers SET balance = balance + ? WHERE account_number = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setDouble(1, -amount);
                statement.setString(2, sender.getAccountNumber());
                statement.executeUpdate();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Update receiver balance (add amount)
            try {
                Connection connection = DatabaseConnection.getConnection();
                String sql = "UPDATE customers SET balance = balance + ? WHERE account_number = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setDouble(1, amount);
                statement.setString(2, receiverAccountNumber);
                statement.executeUpdate();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Get updated sender from database
            Customer updatedSender = null;
            try {
                Connection connection = DatabaseConnection.getConnection();
                String sql = "SELECT * FROM customers WHERE account_number = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, sender.getAccountNumber());
                ResultSet resultSet = statement.executeQuery();
                
                if (resultSet.next()) {
                    updatedSender = new Customer();
                    updatedSender.setId(resultSet.getInt("id"));
                    updatedSender.setAccountNumber(resultSet.getString("account_number"));
                    updatedSender.setUserId(resultSet.getInt("user_id"));
                    updatedSender.setName(resultSet.getString("name"));
                    updatedSender.setEmail(resultSet.getString("email"));
                    updatedSender.setPhone(resultSet.getString("phone"));
                    updatedSender.setAddress(resultSet.getString("address"));
                    updatedSender.setBalance(resultSet.getDouble("balance"));
                }
                
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            session.setAttribute("customer", updatedSender);
            request.setAttribute("success", "Money sent successfully!");
        } else {
            request.setAttribute("error", "Transaction failed");
        }
        
        request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
    }
}
