<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Manage Transactions - Banking System</title>
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
        select, input[type="text"], input[type="number"], textarea { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 3px; }
        textarea { resize: vertical; }
        button { padding: 10px 20px; background: #007bff; color: white; border: none; border-radius: 3px; cursor: pointer; }
        button:hover { background: #0056b3; }
        .error { color: red; margin-bottom: 15px; }
        .success { color: green; margin-bottom: 15px; }
        #receiverField { display: none; }
    </style>
    <script>
        function toggleReceiverField() {
            var type = document.getElementById("transactionType").value;
            var receiverField = document.getElementById("receiverField");
            if (type === "transfer") {
                receiverField.style.display = "block";
            } else {
                receiverField.style.display = "none";
            }
        }
    </script>
</head>
<body>
    <div class="header">
        <h1>Manage Transactions</h1>
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
            <h2>Process Transaction</h2>
            <% String error = (String) request.getAttribute("error"); %>
            <% if (error != null) { %>
                <div class="error"><%= error %></div>
            <% } %>
            <% String success = (String) request.getAttribute("success"); %>
            <% if (success != null) { %>
                <div class="success"><%= success %></div>
            <% } %>
            <form action="manageTransaction" method="post">
                <div class="form-group">
                    <label>Transaction Type *:</label>
                    <select name="transactionType" id="transactionType" required onchange="toggleReceiverField()">
                        <option value="">Select Type</option>
                        <option value="deposit">Deposit</option>
                        <option value="withdrawal">Withdrawal</option>
                        <option value="transfer">Transfer</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Account Number *:</label>
                    <input type="text" name="accountNumber" required>
                </div>
                <div class="form-group" id="receiverField">
                    <label>Receiver Account Number:</label>
                    <input type="text" name="receiverAccountNumber">
                </div>
                <div class="form-group">
                    <label>Amount *:</label>
                    <input type="number" name="amount" step="0.01" min="0.01" required>
                </div>
                <div class="form-group">
                    <label>Note:</label>
                    <textarea name="note" rows="3"></textarea>
                </div>
                <button type="submit">Process Transaction</button>
            </form>
        </div>
    </div>
</body>
</html>

