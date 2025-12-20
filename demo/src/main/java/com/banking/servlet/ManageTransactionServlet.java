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

public class ManageTransactionServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        request.getRequestDispatcher("/admin/manageTransaction.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Get form data
        String transactionType = request.getParameter("transactionType");
        String accountNumber = request.getParameter("accountNumber");
        String receiverAccountNumber = request.getParameter("receiverAccountNumber");
        String amountStr = request.getParameter("amount");
        String note = request.getParameter("note");
        
        // Check if required fields are filled
        if (transactionType == null || accountNumber == null || amountStr == null) {
            request.setAttribute("error", "Please fill all required fields");
            request.getRequestDispatcher("/admin/manageTransaction.jsp").forward(request, response);
            return;
        }
        
        // Convert amount string to number
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                request.setAttribute("error", "Amount must be greater than 0");
                request.getRequestDispatcher("/admin/manageTransaction.jsp").forward(request, response);
                return;
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid amount");
            request.getRequestDispatcher("/admin/manageTransaction.jsp").forward(request, response);
            return;
        }
        
        // Check if account exists
        boolean accountExists = false;
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM customers WHERE account_number = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, accountNumber);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    accountExists = true;
                }
            }
            
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (!accountExists) {
            request.setAttribute("error", "Account not found");
            request.getRequestDispatcher("/admin/manageTransaction.jsp").forward(request, response);
            return;
        }
        
        // Process transaction based on type
        if ("deposit".equals(transactionType)) {
            // Insert transaction
            boolean transactionInserted = false;
            try {
                Connection connection = DatabaseConnection.getConnection();
                String sql = "INSERT INTO transactions (transaction_type, sender_account_number, receiver_account_number, amount, note) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, "deposit");
                statement.setString(2, accountNumber);
                statement.setString(3, null);
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
                // Update balance (add amount)
                try {
                    Connection connection = DatabaseConnection.getConnection();
                    String sql = "UPDATE customers SET balance = balance + ? WHERE account_number = ?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setDouble(1, amount);
                    statement.setString(2, accountNumber);
                    statement.executeUpdate();
                    statement.close();
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                request.setAttribute("success", "Deposit successful!");
            } else {
                request.setAttribute("error", "Transaction failed");
            }
            
        } else if ("withdrawal".equals(transactionType)) {
            // Get customer balance
            Customer customer = null;
            try {
                Connection connection = DatabaseConnection.getConnection();
                String sql = "SELECT * FROM customers WHERE account_number = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, accountNumber);
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
            
            // Check if customer has enough balance
            if (customer != null && customer.getBalance() >= amount) {
                // Insert transaction
                boolean transactionInserted = false;
                try {
                    Connection connection = DatabaseConnection.getConnection();
                    String sql = "INSERT INTO transactions (transaction_type, sender_account_number, receiver_account_number, amount, note) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, "withdrawal");
                    statement.setString(2, accountNumber);
                    statement.setString(3, null);
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
                    // Update balance (subtract amount)
                    try {
                        Connection connection = DatabaseConnection.getConnection();
                        String sql = "UPDATE customers SET balance = balance + ? WHERE account_number = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setDouble(1, -amount);
                        statement.setString(2, accountNumber);
                        statement.executeUpdate();
                        statement.close();
                        connection.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    request.setAttribute("success", "Withdrawal successful!");
                } else {
                    request.setAttribute("error", "Transaction failed");
                }
            } else {
                request.setAttribute("error", "Insufficient balance");
            }
            
        } else if ("transfer".equals(transactionType)) {
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
                request.getRequestDispatcher("/admin/manageTransaction.jsp").forward(request, response);
                return;
            }
            
            // Get sender customer balance
            Customer customer = null;
            try {
                Connection connection = DatabaseConnection.getConnection();
                String sql = "SELECT * FROM customers WHERE account_number = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, accountNumber);
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
            
            // Check if sender has enough balance
            if (customer != null && customer.getBalance() >= amount) {
                // Insert transaction
                boolean transactionInserted = false;
                try {
                    Connection connection = DatabaseConnection.getConnection();
                    String sql = "INSERT INTO transactions (transaction_type, sender_account_number, receiver_account_number, amount, note) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, "transfer");
                    statement.setString(2, accountNumber);
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
                        statement.setString(2, accountNumber);
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
                    request.setAttribute("success", "Transfer successful!");
                } else {
                    request.setAttribute("error", "Transaction failed");
                }
            } else {
                request.setAttribute("error", "Insufficient balance");
            }
        }
        
        request.getRequestDispatcher("/admin/manageTransaction.jsp").forward(request, response);
    }
}
