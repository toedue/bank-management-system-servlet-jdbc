<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <title>Bank - Dashboard</title>
    <style>
      * {
        box-sizing: border-box;
        margin: 0;
        padding: 0;
      }

      body {
        font-family: Arial, Helvetica, sans-serif;
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #f2f4f8;
      }

      .dashboard-wrapper {
        width: 100%;
        max-width: 640px;
        background: #ffffff;
        border-radius: 8px;
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
        padding: 24px 26px;
      }

      .dashboard-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 18px;
      }

      .dashboard-title {
        font-size: 1.4rem;
        color: #1e3c72;
      }

      .logout-link a {
        text-decoration: none;
        font-size: 0.9rem;
        color: #c0392b;
      }

      .logout-link a:hover {
        text-decoration: underline;
      }

      .balance-card {
        background: linear-gradient(135deg, #2a5298, #1e3c72);
        color: #ffffff;
        border-radius: 8px;
        padding: 18px 20px;
        margin-bottom: 18px;
      }

      .balance-label {
        font-size: 0.9rem;
        opacity: 0.85;
      }

      .balance-value {
        font-size: 1.8rem;
        margin-top: 6px;
        font-weight: bold;
      }

      .actions {
        display: flex;
        gap: 10px;
      }

      .btn {
        padding: 9px 16px;
        border-radius: 4px;
        border: none;
        cursor: pointer;
        font-size: 0.95rem;
        text-decoration: none;
        display: inline-block;
        text-align: center;
      }

      .btn-primary {
        background: #2a5298;
        color: #ffffff;
      }

      .btn-primary:hover {
        background: #23457f;
      }
    </style>
  </head>
  <body>
    <div class="dashboard-wrapper">
      <div class="dashboard-header">
        <div class="dashboard-title">
          Welcome, <%= session.getAttribute("username") %>
        </div>
        <div class="logout-link">
          <a href="logout">Logout</a>
        </div>
      </div>

      <div class="balance-card">
        <div class="balance-label">Current Balance</div>
        <div class="balance-value">
          ETB <%= session.getAttribute("balance") %>
        </div>
      </div>

      <div class="actions">
        <a class="btn btn-primary" href="sendMoney">Send Money</a>
      </div>
    </div>
  </body>
</html>
