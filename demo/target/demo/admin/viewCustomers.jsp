<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>View Customers - Banking System</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: #f5f5f5; }
        .header { background: #007bff; color: white; padding: 15px; display: flex; justify-content: space-between; align-items: center; }
        .header a { color: white; text-decoration: none; margin-left: 15px; }
        .container { max-width: 1200px; margin: 20px auto; padding: 20px; }
        .table-container { background: white; padding: 20px; border-radius: 5px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background: #007bff; color: white; }
        tr:hover { background: #f5f5f5; }
        .btn { padding: 5px 10px; text-decoration: none; border-radius: 3px; display: inline-block; margin: 2px; }
        .btn-edit { background: #28a745; color: white; }
        .btn-delete { background: #dc3545; color: white; }
        .error { color: red; margin-bottom: 15px; }
        .success { color: green; margin-bottom: 15px; }
    </style>
</head>
<body>
    <div class="header">
        <h1>View Customers</h1>
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
        <% String error = (String) request.getAttribute("error"); %>
        <% if (error != null) { %>
            <div class="error"><%= error %></div>
        <% } %>
        <% String success = (String) request.getAttribute("success"); %>
        <% if (success != null) { %>
            <div class="success"><%= success %></div>
        <% } %>
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>Account Number</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Address</th>
                        <th>Balance</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% List<Map<String, Object>> customers = (List<Map<String, Object>>) request.getAttribute("customers"); %>
                    <% if (customers != null && !customers.isEmpty()) { %>
                        <% for (Map<String, Object> customer : customers) { %>
                            <tr>
                                <td><%= customer.get("accountNumber") %></td>
                                <td><%= customer.get("name") %></td>
                                <td><%= customer.get("email") %></td>
                                <td><%= customer.get("phone") != null ? customer.get("phone") : "-" %></td>
                                <td><%= customer.get("address") != null ? customer.get("address") : "-" %></td>
                                <td>ETB <%= String.format("%.2f", customer.get("balance")) %></td>
                                <td>
                                    <a href="editCustomer?accountNumber=<%= customer.get("accountNumber") %>" class="btn btn-edit">Edit</a>
                                    <form method="post" action="deleteCustomer" style="display: inline;">
                                        <input type="hidden" name="accountNumber" value="<%= customer.get("accountNumber") %>">
                                        <button type="submit" class="btn btn-delete" onclick="return confirm('Are you sure?')">Delete</button>
                                    </form>
                                </td>
                            </tr>
                        <% } %>
                    <% } else { %>
                        <tr>
                            <td colspan="7" style="text-align: center;">No customers found</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
