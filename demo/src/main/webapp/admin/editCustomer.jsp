<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Customer - Banking System</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: #f5f5f5; }
        .header { background: #007bff; color: white; padding: 15px; display: flex; justify-content: space-between; align-items: center; }
        .header a { color: white; text-decoration: none; margin-left: 15px; }
        .container { max-width: 600px; margin: 20px auto; padding: 20px; }
        .form-container { background: white; padding: 30px; border-radius: 5px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        h2 { margin-bottom: 20px; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; color: #555; }
        input[type="text"], input[type="email"], textarea { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 3px; }
        textarea { resize: vertical; }
        button { padding: 10px 20px; background: #007bff; color: white; border: none; border-radius: 3px; cursor: pointer; }
        button:hover { background: #0056b3; }
        .error { color: red; margin-bottom: 15px; }
        .success { color: green; margin-bottom: 15px; }
    </style>
</head>
<body>
    <div class="header">
        <h1>Edit Customer</h1>
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
        <div class="form-container">
            <h2>Edit Customer</h2>
            <% String error = (String) request.getAttribute("error"); %>
            <% if (error != null) { %>
                <div class="error"><%= error %></div>
            <% } %>
            <% String success = (String) request.getAttribute("success"); %>
            <% if (success != null) { %>
                <div class="success"><%= success %></div>
            <% } %>
            <% Map<String, Object> customer = (Map<String, Object>) request.getAttribute("customer"); %>
            <% if (customer != null) { %>
                <form action="editCustomer" method="post">
                    <input type="hidden" name="accountNumber" value="<%= customer.get("accountNumber") %>">
                    <div class="form-group">
                        <label>Account Number:</label>
                        <input type="text" value="<%= customer.get("accountNumber") %>" disabled>
                    </div>
                    <div class="form-group">
                        <label>Name *:</label>
                        <input type="text" name="name" value="<%= customer.get("name") %>" required>
                    </div>
                    <div class="form-group">
                        <label>Email *:</label>
                        <input type="email" name="email" value="<%= customer.get("email") %>" required>
                    </div>
                    <div class="form-group">
                        <label>Phone:</label>
                        <input type="text" name="phone" value="<%= customer.get("phone") != null ? customer.get("phone") : "" %>">
                    </div>
                    <div class="form-group">
                        <label>Address:</label>
                        <textarea name="address" rows="3"><%= customer.get("address") != null ? customer.get("address") : "" %></textarea>
                    </div>
                    <button type="submit">Update Customer</button>
                </form>
            <% } else { %>
                <p>Customer not found. <a href="viewCustomers">Back to customers</a></p>
            <% } %>
        </div>
    </div>
</body>
</html>
