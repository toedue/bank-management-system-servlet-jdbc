# Banking System - Detailed Code Explanation

## Table of Contents
1. [Project Overview and Flow](#project-overview-and-flow)
2. [Core Concepts Explained](#core-concepts-explained)
   - [JDBC (Java Database Connectivity)](#jdbc-java-database-connectivity)
   - [Sessions](#sessions)
   - [Request Dispatcher](#request-dispatcher)
   - [Redirect](#redirect)
   - [JSP (JavaServer Pages)](#jsp-javaserver-pages)
3. [Database Setup and Schema](#database-setup-and-schema)
4. [Key Code Files Explained](#key-code-files-explained)
   - [DatabaseConnection.java](#databaseconnectionjava)
   - [PasswordUtil.java](#passwordutiljava)
   - [LoginServlet.java](#loginservletjava)
   - [RegisterServlet.java](#registerservletjava)
   - [SendMoneyServlet.java](#sendmoneyservletjava)
   - [ViewCustomersServlet.java](#viewcustomersservletjava)
   - [login.jsp](#loginjsp)
   - [user/dashboard.jsp](#userdashboardjsp)
5. [Complete System Flow](#complete-system-flow)

---

## Project Overview and Flow

This banking system is a web application that allows users to:
- Register and create bank accounts
- Login to access their accounts
- Send money to other users
- View transaction history
- Update their profiles

Admins can:
- View all customers
- Add/edit/delete customers
- View all transactions
- Manage the system

### High-Level Architecture
```
User Browser → Servlet (Controller) → Database/Model
       ↓
    JSP View ← Request Dispatcher
```

The application follows MVC (Model-View-Controller) pattern:
- **Model**: Database tables and DatabaseConnection utility
- **View**: JSP pages
- **Controller**: Servlets that handle requests

---

## Core Concepts Explained

### JDBC (Java Database Connectivity)

JDBC is Java's way of talking to databases. Think of it as a bridge between Java code and database.

#### Key JDBC Classes:
- **Connection**: Opens a connection to the database
- **PreparedStatement**: Executes SQL queries safely
- **ResultSet**: Holds query results
- **DriverManager**: Manages database drivers

#### Basic JDBC Flow:
```java
// 1. Get connection
Connection conn = DatabaseConnection.getConnection();

// 2. Create prepared statement
String sql = "SELECT * FROM users WHERE email = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, email);

// 3. Execute and get results
ResultSet rs = stmt.executeQuery();

// 4. Process results
while (rs.next()) {
    String name = rs.getString("name");
}

// 5. Close everything
rs.close();
stmt.close();
conn.close();
```

#### Why PreparedStatement?
- **Security**: Prevents SQL injection attacks
- **Performance**: Faster for repeated queries
- **Type Safety**: Handles data types automatically

### Sessions

Sessions remember user information across multiple requests. Without sessions, the server would forget who you are after each page load.

#### How Sessions Work:
1. User logs in successfully
2. Server creates a session object
3. Server stores user data in session
4. Server gives browser a session ID (usually in cookie)
5. Browser sends session ID with every request
6. Server uses session ID to retrieve user data

#### Session Code:
```java
// Create/get session
HttpSession session = request.getSession();

// Store data
session.setAttribute("userId", 123);
session.setAttribute("userRole", "user");

// Retrieve data
Integer userId = (Integer) session.getAttribute("userId");

// Remove session (logout)
session.invalidate();
```

#### Session vs Request:
- **Request**: Data lasts for one request only
- **Session**: Data lasts across multiple requests until logout

### Request Dispatcher

Request Dispatcher forwards requests to other resources (like JSP pages) on the server side.

#### Key Characteristics:
- **URL stays the same**: Browser URL doesn't change
- **Server-side forwarding**: Happens internally on server
- **Can pass data**: Use `request.setAttribute()`
- **Same request object**: The JSP gets the same request

#### Usage:
```java
// Forward to a JSP page
request.setAttribute("error", "Invalid login");
request.getRequestDispatcher("/login.jsp").forward(request, response);
```

### Redirect

Redirect tells the browser to make a new request to a different URL.

#### Key Characteristics:
- **URL changes**: Browser URL updates
- **Client-side redirect**: Browser makes new HTTP request
- **Cannot pass data easily**: Data is lost (use session or URL parameters)
- **Prevents form resubmission**: Good after successful actions

#### Usage:
```java
// Redirect to dashboard after login
response.sendRedirect(request.getContextPath() + "/user/dashboard.jsp");
```

#### When to use what:
- **Request Dispatcher**: When showing a page, keeping same URL
- **Redirect**: After actions (login, save data), changing URL

### JSP (JavaServer Pages)

JSP allows embedding Java code in HTML pages. The server processes the Java code and generates HTML.

#### JSP Elements:
- **Scriptlets**: `<% Java code %>`
- **Expressions**: `<%= Java expression %>`
- **Declarations**: `<%! Java declarations %>`
- **Directives**: `<%@ page import="..." %>`

#### Example:
```jsp
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
    <h1>Welcome <%= session.getAttribute("userName") %></h1>
    <% if (session.getAttribute("userRole").equals("admin")) { %>
        <p>You are an admin</p>
    <% } %>
</body>
</html>
```

---

## Database Setup and Schema

### Database Creation
```sql
CREATE DATABASE banking_system;
USE banking_system;
```

### Tables

#### users table
```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### customers table
```sql
CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    balance DECIMAL(15, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

#### transactions table
```sql
CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_type VARCHAR(20) NOT NULL,
    sender_account_number VARCHAR(20),
    receiver_account_number VARCHAR(20),
    amount DECIMAL(15, 2) NOT NULL,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_account_number) REFERENCES customers(account_number) ON DELETE SET NULL,
    FOREIGN KEY (receiver_account_number) REFERENCES customers(account_number) ON DELETE SET NULL
);
```

### Default Admin User
```sql
INSERT INTO users (email, password, role) VALUES
('admin@bank.com', 'admin123', 'admin');
```

---

## Key Code Files Explained

### DatabaseConnection.java

This utility class manages database connections.

```java
package com.banking.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_system?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create and return connection
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }
}
```

**Explanation:**
- `DB_URL`: Connection string with database location and options
- `Class.forName()`: Loads the MySQL JDBC driver
- `DriverManager.getConnection()`: Establishes database connection
- Throws `SQLException` if connection fails

### PasswordUtil.java

Utility for password validation.

```java
package com.banking.util;

public class PasswordUtil {

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }
}
```

**Explanation:**
- Simple validation: password must be at least 8 characters
- Used in registration to enforce minimum password length

### LoginServlet.java

Handles user login requests.

```java
package com.banking.servlet;

import com.banking.util.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginServlet extends HttpServlet {

    // doGet: Show login form
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    // doPost: Process login form submission
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get form data
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validate input
        if (email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Please enter email and password");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // Database variables
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Connect to database
            connection = DatabaseConnection.getConnection();

            // Query for user
            String sql = "SELECT * FROM users WHERE email = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, email);

            resultSet = statement.executeQuery();

            // Check if user exists
            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");

                // Check password (plain text comparison)
                if (password.equals(storedPassword)) {
                    // Login successful
                    int userId = resultSet.getInt("id");
                    String userEmail = resultSet.getString("email");
                    String userRole = resultSet.getString("role");

                    // Create session
                    HttpSession session = request.getSession();
                    session.setAttribute("userId", userId);
                    session.setAttribute("userEmail", userEmail);
                    session.setAttribute("userRole", userRole);

                    // If user, get customer data
                    if ("user".equals(userRole)) {
                        resultSet.close();
                        statement.close();

                        String customerSql = "SELECT * FROM customers WHERE user_id = ?";
                        statement = connection.prepareStatement(customerSql);
                        statement.setInt(1, userId);
                        resultSet = statement.executeQuery();

                        if (resultSet.next()) {
                            session.setAttribute("customerId", resultSet.getInt("id"));
                            session.setAttribute("accountNumber", resultSet.getString("account_number"));
                            session.setAttribute("customerName", resultSet.getString("name"));
                            session.setAttribute("customerEmail", resultSet.getString("email"));
                            session.setAttribute("customerPhone", resultSet.getString("phone"));
                            session.setAttribute("customerAddress", resultSet.getString("address"));
                            session.setAttribute("balance", resultSet.getDouble("balance"));
                        }
                    }

                    // Redirect to appropriate dashboard
                    if ("admin".equals(userRole)) {
                        response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/user/dashboard.jsp");
                    }
                    return;
                }
            }

            // Login failed
            request.setAttribute("error", "Invalid email or password");
            request.getRequestDispatcher("/login.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Login failed. Please try again.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } finally {
            // Close database resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
```

**Explanation:**
- `doGet()`: Shows login form using Request Dispatcher
- `doPost()`: Processes form submission
- Validates input, queries database, checks password
- Creates session with user data on success
- Redirects to dashboard based on role
- Uses try-finally for proper resource cleanup

### RegisterServlet.java

Handles user registration.

```java
// ... (imports same as LoginServlet)

public class RegisterServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get form data
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        // Validate required fields
        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Please fill all required fields");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // Validate password
        if (!PasswordUtil.isValidPassword(password)) {
            request.setAttribute("error", "Password must be at least 8 characters long");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();

            // Check if email already exists
            String checkEmailSql = "SELECT COUNT(*) FROM users WHERE email = ?";
            statement = connection.prepareStatement(checkEmailSql);
            statement.setString(1, email);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    request.setAttribute("error", "Email already registered");
                    request.getRequestDispatcher("/register.jsp").forward(request, response);
                    return;
                }
            }

            resultSet.close();
            statement.close();

            // Insert new user
            String insertUserSql = "INSERT INTO users (email, password, role) VALUES (?, ?, ?)";
            statement = connection.prepareStatement(insertUserSql);
            statement.setString(1, email);
            statement.setString(2, password);
            statement.setString(3, "user");

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted == 0) {
                request.setAttribute("error", "Registration failed. Please try again.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            statement.close();

            // Get user ID
            String getUserSql = "SELECT id FROM users WHERE email = ?";
            statement = connection.prepareStatement(getUserSql);
            statement.setString(1, email);
            resultSet = statement.executeQuery();

            int userId = 0;
            if (resultSet.next()) {
                userId = resultSet.getInt("id");
            }

            resultSet.close();
            statement.close();

            if (userId == 0) {
                request.setAttribute("error", "Registration failed. Please try again.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            // Create customer account
            String accountNumber = "ACC" + System.currentTimeMillis();
            String insertCustomerSql = "INSERT INTO customers (account_number, user_id, name, email, phone, address, balance) VALUES (?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(insertCustomerSql);
            statement.setString(1, accountNumber);
            statement.setInt(2, userId);
            statement.setString(3, name);
            statement.setString(4, email);
            statement.setString(5, phone != null ? phone : "");
            statement.setString(6, address != null ? address : "");
            statement.setDouble(7, 0.0);

            rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                request.setAttribute("success", "Registration successful! Please login.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Registration failed. Please try again.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
```

**Explanation:**
- Validates input and password strength
- Checks for duplicate email
- Creates user record, then customer record
- Generates unique account number using timestamp
- Uses Request Dispatcher to show success/error on login page

### SendMoneyServlet.java

Handles money transfer requests.

```java
// ... (imports)

public class SendMoneyServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check login
        HttpSession session = request.getSession(false);
        if (session == null || !"user".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"user".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Get form data
        String receiverAccountNumber = request.getParameter("receiverAccountNumber");
        String amountStr = request.getParameter("amount");
        String note = request.getParameter("note");

        // Validate input
        if (receiverAccountNumber == null || receiverAccountNumber.trim().isEmpty() ||
            amountStr == null || amountStr.trim().isEmpty()) {
            request.setAttribute("error", "Please fill all required fields");
            request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                request.setAttribute("error", "Amount must be greater than 0");
                request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
                return;
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid amount");
            request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
            return;
        }

        // Get sender account
        String senderAccountNumber = (String) session.getAttribute("accountNumber");

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();

            // Check receiver exists
            String checkReceiverSql = "SELECT COUNT(*) FROM customers WHERE account_number = ?";
            statement = connection.prepareStatement(checkReceiverSql);
            statement.setString(1, receiverAccountNumber);
            resultSet = statement.executeQuery();

            boolean receiverExists = false;
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    receiverExists = true;
                }
            }

            resultSet.close();
            statement.close();

            if (!receiverExists) {
                request.setAttribute("error", "Receiver account not found");
                request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
                return;
            }

            // Check sender balance
            String checkBalanceSql = "SELECT balance FROM customers WHERE account_number = ?";
            statement = connection.prepareStatement(checkBalanceSql);
            statement.setString(1, senderAccountNumber);
            resultSet = statement.executeQuery();

            double senderBalance = 0.0;
            if (resultSet.next()) {
                senderBalance = resultSet.getDouble("balance");
            }

            resultSet.close();
            statement.close();

            if (senderBalance < amount) {
                request.setAttribute("error", "Insufficient balance");
                request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
                return;
            }

            // Record transaction
            String insertTransactionSql = "INSERT INTO transactions (transaction_type, sender_account_number, receiver_account_number, amount, note) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(insertTransactionSql);
            statement.setString(1, "transfer");
            statement.setString(2, senderAccountNumber);
            statement.setString(3, receiverAccountNumber);
            statement.setDouble(4, amount);
            statement.setString(5, note != null ? note : "");

            int rowsInserted = statement.executeUpdate();
            statement.close();

            if (rowsInserted > 0) {
                // Update sender balance
                String updateSenderSql = "UPDATE customers SET balance = balance - ? WHERE account_number = ?";
                statement = connection.prepareStatement(updateSenderSql);
                statement.setDouble(1, amount);
                statement.setString(2, senderAccountNumber);
                statement.executeUpdate();
                statement.close();

                // Update receiver balance
                String updateReceiverSql = "UPDATE customers SET balance = balance + ? WHERE account_number = ?";
                statement = connection.prepareStatement(updateReceiverSql);
                statement.setDouble(1, amount);
                statement.setString(2, receiverAccountNumber);
                statement.executeUpdate();
                statement.close();

                // Update session balance
                String getUpdatedBalanceSql = "SELECT balance FROM customers WHERE account_number = ?";
                statement = connection.prepareStatement(getUpdatedBalanceSql);
                statement.setString(1, senderAccountNumber);
                resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    double newBalance = resultSet.getDouble("balance");
                    session.setAttribute("balance", newBalance);
                }

                request.setAttribute("success", "Money sent successfully!");
            } else {
                request.setAttribute("error", "Transaction failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Transaction failed. Please try again.");
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
    }
}
```

**Explanation:**
- Validates user session and input
- Checks receiver account exists
- Verifies sufficient balance
- Records transaction, updates balances atomically
- Updates session balance for UI
- Uses Request Dispatcher to show result on same page

### ViewCustomersServlet.java

Admin servlet to view all customers.

```java
// ... (imports)

public class ViewCustomersServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check admin login
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Get all customers
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<Map<String, Object>> customers = new ArrayList<>();

        try {
            connection = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM customers ORDER BY created_at DESC";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            // Process results
            while (resultSet.next()) {
                Map<String, Object> customer = new HashMap<>();
                customer.put("id", resultSet.getInt("id"));
                customer.put("accountNumber", resultSet.getString("account_number"));
                customer.put("userId", resultSet.getInt("user_id"));
                customer.put("name", resultSet.getString("name"));
                customer.put("email", resultSet.getString("email"));
                customer.put("phone", resultSet.getString("phone"));
                customer.put("address", resultSet.getString("address"));
                customer.put("balance", resultSet.getDouble("balance"));
                customers.add(customer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Pass data to JSP
        request.setAttribute("customers", customers);
        request.getRequestDispatcher("/admin/viewCustomers.jsp").forward(request, response);
    }
}
```

**Explanation:**
- Checks admin session
- Queries all customers ordered by creation date
- Stores results in List of Maps
- Passes data to JSP using request attributes
- Uses Request Dispatcher to show results

### login.jsp

Login page with form.

```jsp
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>Bank - Login</title>
    <style>
        /* CSS styles for form */
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: Arial, Helvetica, sans-serif;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(135deg, #1e3c72, #2a5298);
            color: #222;
        }
        .auth-wrapper {
            width: 100%;
            max-width: 400px;
            background: #ffffff;
            border-radius: 8px;
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
            padding: 30px 28px 26px;
        }
        /* ... more CSS ... */
    </style>
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-header">
            <div class="auth-title">Welcome</div>
            <div class="auth-subtitle">Sign in to your account</div>
        </div>

        <form action="login" method="post">
            <div class="form-group">
                <label for="email">Email</label>
                <input
                    id="email"
                    type="text"
                    name="email"
                    required
                    autocomplete="off"
                />
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input id="password" type="password" name="password" required />
            </div>

            <button class="btn-primary" type="submit">Login</button>
        </form>

        <p class="error">
            <%= request.getAttribute("error") != null ?
                request.getAttribute("error") : "" %>
        </p>
    </div>
</body>
</html>
```

**Explanation:**
- HTML form with email/password fields
- CSS for styling (inline)
- Form submits to "login" (mapped to LoginServlet)
- Displays error message from servlet using JSP expression

### user/dashboard.jsp

User dashboard showing account info and transactions.

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>User Dashboard - Banking System</title>
    <style>
        /* CSS styles */
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: #f5f5f5; }
        .header {
            background: #28a745; color: white; padding: 15px;
            display: flex; justify-content: space-between; align-items: center;
        }
        .header a { color: white; text-decoration: none; margin-left: 15px; }
        .container { max-width: 1200px; margin: 20px auto; padding: 20px; }
        .account-info {
            background: white; padding: 20px; border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1); margin-bottom: 20px;
        }
        /* ... more CSS ... */
    </style>
</head>
<body>
    <div class="header">
        <h1>Customer Dashboard</h1>
        <div>
            <a href="dashboard">Dashboard</a>
            <a href="sendMoney">Send Money</a>
            <a href="updateProfile">Update Profile</a>
            <a href="viewTransactions">View Transactions</a>
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </div>
    </div>
    <div class="container">
        <div class="account-info">
            <h2>Account Information</h2>
            <div class="account-details">
                <div class="detail-item">
                    <label>Account Number</label>
                    <p><%= session.getAttribute("accountNumber") %></p>
                </div>
                <div class="detail-item">
                    <label>Name</label>
                    <p><%= session.getAttribute("customerName") %></p>
                </div>
                <div class="detail-item">
                    <label>Balance</label>
                    <p style="color: #28a745;">ETB <%= String.format("%.2f", session.getAttribute("balance")) %></p>
                </div>
            </div>
        </div>
        <div class="menu">
            <h2>Quick Actions</h2>
            <ul>
                <li><a href="sendMoney">Send Money to Another User</a></li>
                <li><a href="updateProfile">Update Profile</a></li>
                <li><a href="viewTransactions">View Transaction History</a></li>
            </ul>
        </div>
        <div class="transactions">
            <h2>Recent Transactions</h2>
            <% List<Map<String, Object>> transactions = (List<Map<String, Object>>) request.getAttribute("recentTransactions"); %>
            <% if (transactions != null && !transactions.isEmpty()) { %>
                <table>
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Type</th>
                            <th>Amount</th>
                            <th>Details</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% String accountNum = (String) session.getAttribute("accountNumber"); %>
                        <% for (Map<String, Object> t : transactions) { %>
                            <tr>
                                <td><%= new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(t.get("createdAt")) %></td>
                                <td><%= t.get("transactionType") %></td>
                                <td>ETB <%= String.format("%.2f", t.get("amount")) %></td>
                                <td>
                                    <% if ("transfer".equals(t.get("transactionType"))) { %>
                                        <% if (accountNum != null && accountNum.equals(t.get("senderAccountNumber"))) { %>
                                            To: <%= t.get("receiverAccountNumber") %>
                                        <% } else { %>
                                            From: <%= t.get("senderAccountNumber") %>
                                        <% } %>
                                    <% } else { %>
                                        <%= t.get("note") != null ? t.get("note") : "-" %>
                                    <% } %>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } else { %>
                <p>No recent transactions</p>
            <% } %>
        </div>
    </div>
</body>
</html>
```

**Explanation:**
- Uses JSP directives to import classes
- Displays session data (account info)
- Navigation menu with links to other pages
- Conditional rendering of transactions table
- Uses scriptlets for logic and expressions for output
- Formats data and handles null values

---

## Complete System Flow

### 1. User Registration Flow
```
User visits /register
    ↓
RegisterServlet.doGet()
    ↓
Shows register.jsp
    ↓
User fills form, submits
    ↓
RegisterServlet.doPost()
    ↓
Validate input
    ↓
Check email uniqueness
    ↓
Insert into users table
    ↓
Insert into customers table
    ↓
Show success on login.jsp
```

### 2. User Login Flow
```
User visits /login
    ↓
LoginServlet.doGet()
    ↓
Shows login.jsp
    ↓
User submits credentials
    ↓
LoginServlet.doPost()
    ↓
Query users table
    ↓
Verify password
    ↓
Create session with user data
    ↓
Query customers table (if user)
    ↓
Add customer data to session
    ↓
Redirect to dashboard
```

### 3. Money Transfer Flow
```
Logged-in user visits /user/sendMoney
    ↓
SendMoneyServlet.doGet()
    ↓
Check session (must be user)
    ↓
Show sendMoney.jsp
    ↓
User fills form, submits
    ↓
SendMoneyServlet.doPost()
    ↓
Validate input and session
    ↓
Check receiver exists
    ↓
Check sufficient balance
    ↓
Insert transaction record
    ↓
Update sender balance (-)
    ↓
Update receiver balance (+)
    ↓
Update session balance
    ↓
Show success on sendMoney.jsp
```

### 4. Admin View Customers Flow
```
Admin visits /admin/viewCustomers
    ↓
ViewCustomersServlet.doGet()
    ↓
Check session (must be admin)
    ↓
Query all customers
    ↓
Store in List<Map>
    ↓
Set as request attribute
    ↓
Forward to viewCustomers.jsp
    ↓
JSP displays customer table
```

