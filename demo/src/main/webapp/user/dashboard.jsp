<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>User Dashboard - Banking System</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: #f5f5f5; }
        .header { background: #28a745; color: white; padding: 15px; display: flex; justify-content: space-between; align-items: center; }
        .header a { color: white; text-decoration: none; margin-left: 15px; }
        .container { max-width: 1200px; margin: 20px auto; padding: 20px; }
        .account-info { background: white; padding: 20px; border-radius: 5px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .account-info h2 { margin-bottom: 15px; }
        .account-details { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; }
        .detail-item label { color: #666; display: block; margin-bottom: 5px; }
        .detail-item p { font-size: 18px; color: #333; font-weight: bold; }
        .menu { background: white; padding: 20px; border-radius: 5px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .menu h2 { margin-bottom: 15px; }
        .menu ul { list-style: none; }
        .menu li { margin: 10px 0; }
        .menu a { color: #28a745; text-decoration: none; padding: 10px; display: block; border: 1px solid #ddd; border-radius: 3px; }
        .menu a:hover { background: #f5f5f5; }
        .transactions { background: white; padding: 20px; border-radius: 5px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background: #28a745; color: white; }
        tr:hover { background: #f5f5f5; }
    </style>
</head>
<body>
    <div class="header">
        <h1>Customer Dashboard</h1>
        <div>
            <a href="dashboard">Dashboard</a>
            <a href="sendMoney">Send Money</a>
            <a href="updateProfile">Update Profile</a>
            <a href="viewTransactions">View Transactions</a>
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </div>
    </div>
    <div class="container">
        <div class="account-info">
            <h2>Account Information</h2>
            <div class="account-details">
                <div class="detail-item">
                    <label>Account Number</label>
                    <p><%= session.getAttribute("accountNumber") %></p>
                </div>
                <div class="detail-item">
                    <label>Name</label>
                    <p><%= session.getAttribute("customerName") %></p>
                </div>
                <div class="detail-item">
                    <label>Balance</label>
                    <p style="color: #28a745;">ETB <%= String.format("%.2f", session.getAttribute("balance")) %></p>
                </div>
            </div>
        </div>
        <div class="menu">
            <h2>Quick Actions</h2>
            <ul>
                <li><a href="sendMoney">Send Money to Another User</a></li>
                <li><a href="updateProfile">Update Profile</a></li>
                <li><a href="viewTransactions">View Transaction History</a></li>
            </ul>
        </div>
        <div class="transactions">
            <h2>Recent Transactions</h2>
            <% List<Map<String, Object>> transactions = (List<Map<String, Object>>) request.getAttribute("recentTransactions"); %>
            <% if (transactions != null && !transactions.isEmpty()) { %>
                <table>
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Type</th>
                            <th>Amount</th>
                            <th>Details</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% String accountNum = (String) session.getAttribute("accountNumber"); %>
                        <% for (Map<String, Object> t : transactions) { %>
                            <tr>
                                <td><%= new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(t.get("createdAt")) %></td>
                                <td><%= t.get("transactionType") %></td>
                                <td>ETB <%= String.format("%.2f", t.get("amount")) %></td>
                                <td>
                                    <% if ("transfer".equals(t.get("transactionType"))) { %>
                                        <% if (accountNum != null && accountNum.equals(t.get("senderAccountNumber"))) { %>
                                            To: <%= t.get("receiverAccountNumber") %>
                                        <% } else { %>
                                            From: <%= t.get("senderAccountNumber") %>
                                        <% } %>
                                    <% } else { %>
                                        <%= t.get("note") != null ? t.get("note") : "-" %>
                                    <% } %>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } else { %>
                <p>No recent transactions</p>
            <% } %>
        </div>
    </div>
</body>
</html>
