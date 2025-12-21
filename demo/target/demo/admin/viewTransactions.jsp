<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>View All Transactions - Banking System</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: #f5f5f5; }
        .header { background: #007bff; color: white; padding: 15px; display: flex; justify-content: space-between; align-items: center; }
        .header a { color: white; text-decoration: none; margin-left: 15px; }
        .container { max-width: 1400px; margin: 20px auto; padding: 20px; }
        .table-container { background: white; padding: 20px; border-radius: 5px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; min-width: 1000px; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background: #007bff; color: white; position: sticky; top: 0; }
        tr:hover { background: #f5f5f5; }
        .transaction-type { padding: 5px 10px; border-radius: 3px; font-weight: bold; display: inline-block; }
        .deposit { background: #d4edda; color: #155724; }
        .withdrawal { background: #f8d7da; color: #721c24; }
        .transfer { background: #d1ecf1; color: #0c5460; }
        .amount { font-weight: bold; }
        .positive { color: #28a745; }
        .negative { color: #dc3545; }
        .info { background: #e7f3ff; padding: 15px; border-radius: 5px; margin-bottom: 20px; }
    </style>
</head>
<body>
    <div class="header">
        <h1>View All Transactions</h1>
        <div>
            <a href="dashboard">Dashboard</a>
            <a href="viewCustomers">View Customers</a>
            <a href="addCustomer">Add Customer</a>
            <a href="manageTransaction">Manage Transactions</a>
            <a href="viewTransactions">View Transactions</a>
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </div>
    </div>
    <div class="container">
        <div class="info">
            <h2>All Banking Transactions</h2>
            <p>This page shows all transactions across all customer accounts in the system.</p>
        </div>
        <div class="table-container">
            <% List<Map<String, Object>> transactions = (List<Map<String, Object>>) request.getAttribute("transactions"); %>
            <% if (transactions != null && !transactions.isEmpty()) { %>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Date & Time</th>
                            <th>Type</th>
                            <th>Sender Account</th>
                            <th>Receiver Account</th>
                            <th>Amount</th>
                            <th>Note</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Map<String, Object> t : transactions) { %>
                            <tr>
                                <td><%= t.get("id") %></td>
                                <td><%= new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(t.get("createdAt")) %></td>
                                <td>
                                    <span class="transaction-type <%= t.get("transactionType") %>">
                                        <%= ((String)t.get("transactionType")).toUpperCase() %>
                                    </span>
                                </td>
                                <td><%= t.get("senderAccountNumber") != null ? t.get("senderAccountNumber") : "-" %></td>
                                <td><%= t.get("receiverAccountNumber") != null ? t.get("receiverAccountNumber") : "-" %></td>
                                <td class="amount positive">ETB <%= String.format("%.2f", t.get("amount")) %></td>
                                <td><%= t.get("note") != null && !((String)t.get("note")).isEmpty() ? t.get("note") : "-" %></td>
                            </tr>
                        <% } %>
                    </tbody>
                    <tfoot>
                        <tr>
                            <td colspan="7" style="text-align: center; padding: 15px; font-weight: bold;">
                                Total Transactions: <%= transactions.size() %>
                            </td>
                        </tr>
                    </tfoot>
                </table>
            <% } else { %>
                <p style="text-align: center; padding: 40px; color: #666;">
                    No transactions found in the system.
                </p>
            <% } %>
        </div>
    </div>
</body>
</html>
