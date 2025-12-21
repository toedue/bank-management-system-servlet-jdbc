<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
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
            <% String accountNumber = (String) session.getAttribute("accountNumber"); %>
            <% List<Map<String, Object>> transactions = (List<Map<String, Object>>) request.getAttribute("transactions"); %>
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
                        <% for (Map<String, Object> t : transactions) { %>
                            <tr>
                                <td><%= new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(t.get("createdAt")) %></td>
                                <td><%= t.get("transactionType") %></td>
                                <td class="<%= (accountNumber != null && accountNumber.equals(t.get("senderAccountNumber"))) ? "debit" : "credit" %>">
                                    <% if (accountNumber != null && accountNumber.equals(t.get("senderAccountNumber"))) { %>
                                        -ETB <%= String.format("%.2f", t.get("amount")) %>
                                    <% } else { %>
                                        +ETB <%= String.format("%.2f", t.get("amount")) %>
                                    <% } %>
                                </td>
                                <td>
                                    <% if ("transfer".equals(t.get("transactionType"))) { %>
                                        <% if (accountNumber != null && accountNumber.equals(t.get("senderAccountNumber"))) { %>
                                            To: <%= t.get("receiverAccountNumber") %>
                                        <% } else { %>
                                            From: <%= t.get("senderAccountNumber") %>
                                        <% } %>
                                    <% } else { %>
                                        <%= t.get("transactionType") %>
                                    <% } %>
                                </td>
                                <td><%= t.get("note") != null ? t.get("note") : "-" %></td>
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
