<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Send Money</title>
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

        .transfer-wrapper {
            width: 100%;
            max-width: 480px;
            background: #ffffff;
            border-radius: 8px;
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
            padding: 24px 26px;
        }

        h2 {
            margin-bottom: 14px;
            color: #1e3c72;
        }

        .form-group {
            margin-top: 12px;
        }

        label {
            display: block;
            margin-bottom: 4px;
            font-size: 0.9rem;
            color: #444;
        }

        input[type="number"],
        input[type="text"] {
            width: 100%;
            padding: 9px 10px;
            border-radius: 4px;
            border: 1px solid #ccd2e3;
            font-size: 0.95rem;
        }

        input:focus {
            outline: none;
            border-color: #2a5298;
            box-shadow: 0 0 0 2px rgba(42, 82, 152, 0.15);
        }

        .btn-primary {
            margin-top: 18px;
            padding: 10px 16px;
            border-radius: 4px;
            border: none;
            background: #2a5298;
            color: #ffffff;
            font-size: 0.95rem;
            cursor: pointer;
        }

        .btn-primary:hover {
            background: #23457f;
        }

        .footer-links {
            margin-top: 16px;
            font-size: 0.9rem;
        }

        .footer-links a {
            color: #2a5298;
            text-decoration: none;
        }

        .footer-links a:hover {
            text-decoration: underline;
        }

        .msg {
            margin-top: 12px;
            min-height: 18px;
            font-size: 0.9rem;
            color: #2c3e50;
        }
    </style>
</head>
<body>
<div class="transfer-wrapper">
    <h2>Send Money</h2>

    <form action="sendMoney" method="post">
        <div class="form-group">
            <label for="receiver">Receiver Account</label>
            <input id="receiver" type="text" name="receiver" required>
        </div>

        <div class="form-group">
            <label for="amount">Amount (ETB)</label>
            <input id="amount" type="number" name="amount" min="1" step="1" required>
        </div>

        <button class="btn-primary" type="submit">Send</button>
    </form>

    <div class="msg">
        <%= request.getAttribute("message") != null ? request.getAttribute("message") : "" %>
    </div>

    <div class="footer-links">
        <a href="dashboard">&larr; Back to Dashboard</a>
    </div>
</div>
</body>
</html>
