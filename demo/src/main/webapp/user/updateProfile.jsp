<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Update Profile - Banking System</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: #f5f5f5; }
        .header { background: #28a745; color: white; padding: 15px; display: flex; justify-content: space-between; align-items: center; }
        .header a { color: white; text-decoration: none; margin-left: 15px; }
        .container { max-width: 600px; margin: 20px auto; padding: 20px; }
        .form-container { background: white; padding: 30px; border-radius: 5px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        h2 { margin-bottom: 20px; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; color: #555; }
        input[type="text"], input[type="password"], textarea { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 3px; }
        textarea { resize: vertical; }
        button { padding: 10px 20px; background: #28a745; color: white; border: none; border-radius: 3px; cursor: pointer; }
        button:hover { background: #218838; }
        .error { color: red; margin-bottom: 15px; }
        .success { color: green; margin-bottom: 15px; }
    </style>
</head>
<body>
    <div class="header">
        <h1>Update Profile</h1>
        <div>
            <a href="dashboard">Dashboard</a>
            <a href="sendMoney">Send Money</a>
            <a href="updateProfile">Update Profile</a>
            <a href="viewTransactions">View Transactions</a>
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </div>
    </div>
    <div class="container">
        <div class="form-container">
            <h2>Update Your Profile</h2>
            <% String error = (String) request.getAttribute("error"); %>
            <% if (error != null) { %>
                <div class="error"><%= error %></div>
            <% } %>
            <% String success = (String) request.getAttribute("success"); %>
            <% if (success != null) { %>
                <div class="success"><%= success %></div>
            <% } %>
            <% if (request.getAttribute("accountNumber") != null) { %>
                <form action="updateProfile" method="post">
                    <div class="form-group">
                        <label>Account Number:</label>
                        <input type="text" value="<%= request.getAttribute("accountNumber") %>" disabled>
                    </div>
                    <div class="form-group">
                        <label>Name *:</label>
                        <input type="text" name="name" value="<%= request.getAttribute("customerName") %>" required>
                    </div>
                    <div class="form-group">
                        <label>Email:</label>
                        <input type="email" value="<%= request.getAttribute("customerEmail") %>" disabled>
                    </div>
                    <div class="form-group">
                        <label>Phone:</label>
                        <input type="text" name="phone" value="<%= request.getAttribute("customerPhone") != null ? request.getAttribute("customerPhone") : "" %>">
                    </div>
                    <div class="form-group">
                        <label>Address:</label>
                        <textarea name="address" rows="3"><%= request.getAttribute("customerAddress") != null ? request.getAttribute("customerAddress") : "" %></textarea>
                    </div>
                    <div class="form-group">
                        <label>New Password (leave blank to keep current):</label>
                        <input type="password" name="password">
                    </div>
                    <button type="submit">Update Profile</button>
                </form>
            <% } else { %>
                <p>Customer information not found.</p>
            <% } %>
        </div>
    </div>
</body>
</html>
