<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Send Money - Banking System</title>
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
        input[type="text"], input[type="number"], textarea { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 3px; }
        textarea { resize: vertical; }
        button { padding: 10px 20px; background: #28a745; color: white; border: none; border-radius: 3px; cursor: pointer; }
        button:hover { background: #218838; }
        .error { color: red; margin-bottom: 15px; }
        .success { color: green; margin-bottom: 15px; }
    </style>
</head>
<body>
    <div class="header">
        <h1>Send Money</h1>
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
            <h2>Send Money to Another User</h2>
            <% String error = (String) request.getAttribute("error"); %>
            <% if (error != null) { %>
                <div class="error"><%= error %></div>
            <% } %>
            <% String success = (String) request.getAttribute("success"); %>
            <% if (success != null) { %>
                <div class="success"><%= success %></div>
            <% } %>
            <form action="sendMoney" method="post">
                <div class="form-group">
                    <label>Receiver Account Number *:</label>
                    <input type="text" name="receiverAccountNumber" required>
                </div>
                <div class="form-group">
                    <label>Amount *:</label>
                    <input type="number" name="amount" step="0.01" min="0.01" required>
                </div>
                <div class="form-group">
                    <label>Note:</label>
                    <textarea name="note" rows="3"></textarea>
                </div>
                <button type="submit">Send Money</button>
            </form>
        </div>
    </div>
</body>
</html>

