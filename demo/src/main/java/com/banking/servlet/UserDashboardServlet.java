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

public class UserDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || !"user".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Get customer from session or database
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            Integer userId = (Integer) session.getAttribute("userId");
            try {
                Connection connection = DatabaseConnection.getConnection();
                String sql = "SELECT * FROM customers WHERE user_id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();
                
                if (resultSet.next()) {
                    customer = new Customer();
                    customer.setId(resultSet.getInt("id"));
                    customer.setAccountNumber(resultSet.getString("account_number"));
                    customer.setUserId(resultSet.getInt("user_id"));
                    customer.setName(resultSet.getString("name"));
                    customer.setEmail(resultSet.getString("email"));
                    customer.setPhone(resultSet.getString("phone"));
                    customer.setAddress(resultSet.getString("address"));
                    customer.setBalance(resultSet.getDouble("balance"));
                }
                
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            session.setAttribute("customer", customer);
        }

        if (customer != null) {
            request.setAttribute("customer", customer);

            // Get all transactions for this account
            List<Transaction> allTransactions = new ArrayList<>();
            try {
                Connection connection = DatabaseConnection.getConnection();
                String sql = "SELECT * FROM transactions WHERE sender_account_number = ? OR receiver_account_number = ? ORDER BY created_at DESC";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, customer.getAccountNumber());
                statement.setString(2, customer.getAccountNumber());
                ResultSet resultSet = statement.executeQuery();
                
                while (resultSet.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setId(resultSet.getInt("id"));
                    transaction.setTransactionType(resultSet.getString("transaction_type"));
                    transaction.setSenderAccountNumber(resultSet.getString("sender_account_number"));
                    transaction.setReceiverAccountNumber(resultSet.getString("receiver_account_number"));
                    transaction.setAmount(resultSet.getDouble("amount"));
                    transaction.setNote(resultSet.getString("note"));
                    transaction.setCreatedAt(resultSet.getTimestamp("created_at"));
                    allTransactions.add(transaction);
                }
                
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Get only first 5 transactions
            List<Transaction> recentTransactions = new ArrayList<>();
            int maxItems = 5;
            int index = 0;
            while (index < allTransactions.size() && index < maxItems) {
                recentTransactions.add(allTransactions.get(index));
                index++;
            }

            request.setAttribute("recentTransactions", recentTransactions);
        }

        request.getRequestDispatcher("/user/dashboard.jsp").forward(request, response);
    }
}
