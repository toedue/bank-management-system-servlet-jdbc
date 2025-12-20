<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register - Banking System</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: #f2f4f8; padding: 40px; }
        .container { max-width: 520px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 8px 20px rgba(0,0,0,0.08); }
        h2 { text-align: center; color: #1e3c72; margin-bottom: 18px; }
        .form-group { margin-bottom: 14px; }
        label { display: block; margin-bottom: 5px; color: #555; font-size: 0.9rem; }
        input[type="text"], input[type="email"], input[type="password"], textarea {
            width: 100%; padding: 9px 10px; border: 1px solid #ccd2e3; border-radius: 4px; font-size: 0.95rem;
        }
        textarea { resize: vertical; }
        input:focus, textarea:focus {
            outline: none; border-color: #2a5298; box-shadow: 0 0 0 2px rgba(42,82,152,0.15);
        }
        button { width: 100%; padding: 10px; background: #2a5298; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 1rem; margin-top: 4px; }
        button:hover { background: #23457f; }
        .error { color: red; margin-bottom: 10px; text-align: center; font-size: 0.9rem; }
        .link { text-align: center; margin-top: 12px; font-size: 0.9rem; }
        .link a { color: #2a5298; text-decoration: none; }
        .link a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="container">
        <h2>Customer Registration</h2>
        <% String error = (String) request.getAttribute("error"); %>
        <% if (error != null) { %>
            <div class="error"><%= error %></div>
        <% } %>
        <form action="register" method="post">
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
            <button type="submit">Register</button>
        </form>
        <div class="link">
            <a href="login.jsp">Already have an account? Login here</a>
        </div>
    </div>
</body>
</html>
