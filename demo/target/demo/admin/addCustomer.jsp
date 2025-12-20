<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Customer - Banking System</title>
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
        input[type="text"], input[type="email"], input[type="password"], input[type="number"], textarea { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 3px; }
        textarea { resize: vertical; }
        button { padding: 10px 20px; background: #007bff; color: white; border: none; border-radius: 3px; cursor: pointer; }
        button:hover { background: #0056b3; }
        .error { color: red; margin-bottom: 15px; }
        .success { color: green; margin-bottom: 15px; }
    </style>
</head>
<body>
    <div class="header">
        <h1>Add Customer</h1>
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
            <h2>Add New Customer</h2>
            <% String error = (String) request.getAttribute("error"); %>
            <% if (error != null) { %>
                <div class="error"><%= error %></div>
            <% } %>
            <% String success = (String) request.getAttribute("success"); %>
            <% if (success != null) { %>
                <div class="success"><%= success %></div>
            <% } %>
            <form action="addCustomer" method="post">
                <div class="form-group">
                    <label>Name *:</label>
                    <input type="text" name="name" required>
                </div>
                <div class="form-group">
                    <label>Email *:</label>
                    <input type="email" name="email" required>
                </div>
                <div class="form-group">
                    <label>Password *:</label>
                    <input type="password" name="password" required>
                </div>
                <div class="form-group">
                    <label>Phone:</label>
                    <input type="text" name="phone">
                </div>
                <div class="form-group">
                    <label>Address:</label>
                    <textarea name="address" rows="3"></textarea>
                </div>
                <div class="form-group">
                    <label>Initial Balance:</label>
                    <input type="number" name="balance" step="0.01" min="0" value="0">
                </div>
                <button type="submit">Add Customer</button>
            </form>
        </div>
    </div>
</body>
</html>

