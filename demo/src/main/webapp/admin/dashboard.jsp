<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard - Banking System</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: #f5f5f5; }
        .header { background: #007bff; color: white; padding: 15px; display: flex; justify-content: space-between; align-items: center; }
        .header a { color: white; text-decoration: none; margin-left: 15px; }
        .container { max-width: 1200px; margin: 20px auto; padding: 20px; }
        .stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-bottom: 30px; }
        .stat-card { background: white; padding: 20px; border-radius: 5px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .stat-card h3 { color: #666; margin-bottom: 10px; }
        .stat-card p { font-size: 32px; color: #007bff; font-weight: bold; }
        .menu { background: white; padding: 20px; border-radius: 5px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .menu h2 { margin-bottom: 15px; }
        .menu ul { list-style: none; }
        .menu li { margin: 10px 0; }
        .menu a { color: #007bff; text-decoration: none; padding: 10px; display: block; border: 1px solid #ddd; border-radius: 3px; }
        .menu a:hover { background: #f5f5f5; }
    </style>
</head>
<body>
    <div class="header">
        <h1>Admin Dashboard</h1>
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
        <div class="stats">
            <div class="stat-card">
                <h3>Total Customers</h3>
                <p><%= request.getAttribute("totalCustomers") != null ? request.getAttribute("totalCustomers") : 0 %></p>
            </div>
            <div class="stat-card">
                <h3>Total Transactions</h3>
                <p><%= request.getAttribute("totalTransactions") != null ? request.getAttribute("totalTransactions") : 0 %></p>
            </div>
        </div>
        <div class="menu">
            <h2>Quick Actions</h2>
            <ul>
                <li><a href="addCustomer">Add New Customer</a></li>
                <li><a href="viewCustomers">View All Customers</a></li>
                <li><a href="manageTransaction">Manage Transactions</a></li>
                <li><a href="viewTransactions">View All Transactions</a></li>
            </ul>
        </div>
    </div>
</body>
</html>

