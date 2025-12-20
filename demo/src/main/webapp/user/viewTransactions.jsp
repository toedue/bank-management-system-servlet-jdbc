<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.banking.model.Transaction" %>
<%@ page import="com.banking.model.Customer" %>
<!DOCTYPE html>
<html>
<head>
    <title>Transaction History - Banking System</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: #f5f5f5; }
        .header { background: #28a745; color: white; padding: 15px; display: flex; justify-content: space-between; align-items: center; }
        .header a { color: white; text-decoration: none; margin-left: 15px; }
        .container { max-width: 1200px; margin: 20px auto; padding: 20px; }
        .table-container { background: white; padding: 20px; border-radius: 5px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background: #28a745; color: white; }
        tr:hover { background: #f5f5f5; }
        .debit { color: red; }
        .credit { color: green; }
    </style>
</head>
<body>
    <div class="header">
        <h1>Transaction History</h1>
        <div>
            <a href="dashboard">Dashboard</a>
            <a href="sendMoney">Send Money</a>
            <a href="updateProfile">Update Profile</a>
            <a href="viewTransactions">View Transactions</a>
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </div>
    </div>
    <div class="container">
        <div class="table-container">
            <% Customer customer = (Customer) session.getAttribute("customer"); %>
            <% List<Transaction> transactions = (List<Transaction>) request.getAttribute("transactions"); %>
            <% if (transactions != null && !transactions.isEmpty()) { %>
                <table>
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Type</th>
                            <th>Amount</th>
                            <th>Details</th>
                            <th>Note</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Transaction t : transactions) { %>
                            <tr>
                                <td><%= new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(t.getCreatedAt()) %></td>
                                <td><%= t.getTransactionType() %></td>
                                <td class="<%= (customer != null && customer.getAccountNumber().equals(t.getSenderAccountNumber())) ? "debit" : "credit" %>">
                                    <% if (customer != null && customer.getAccountNumber().equals(t.getSenderAccountNumber())) { %>
                                        -ETB <%= String.format("%.2f", t.getAmount()) %>
                                    <% } else { %>
                                        +ETB <%= String.format("%.2f", t.getAmount()) %>
                                    <% } %>
                                </td>
                                <td>
                                    <% if ("transfer".equals(t.getTransactionType())) { %>
                                        <% if (customer != null && customer.getAccountNumber().equals(t.getSenderAccountNumber())) { %>
                                            To: <%= t.getReceiverAccountNumber() %>
                                        <% } else { %>
                                            From: <%= t.getSenderAccountNumber() %>
                                        <% } %>
                                    <% } else { %>
                                        <%= t.getTransactionType() %>
                                    <% } %>
                                </td>
                                <td><%= t.getNote() != null ? t.getNote() : "-" %></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } else { %>
                <p>No transactions found</p>
            <% } %>
        </div>
    </div>
</body>
</html>

