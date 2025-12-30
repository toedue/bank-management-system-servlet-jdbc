# 🏦 Java Bank Management System: A Complete Beginner's Guide

Welcome! This guide is designed to help you understand every single detail of your project. We won't skip anything. We will look at the code line-by-line, explaining not just *what* the code does, but *where* the magic comes from.

---

## 🏗️ Phase 1: The Foundation (Structure & Environment)

Before we look at the code, you need to understand the "skeleton" of the project and the tools that make it run.

### 1. The Tools (The "Where" and "How")
To run this project, you are using three main technologies:
1.  **JDK (Java Development Kit)**: This is the engine that runs Java. It provides the standard libraries like `java.io.*` and `java.sql.*`.
2.  **MySQL**: This is the "Brain" where all the bank data (users, accounts, money) is stored.
3.  **Apache Tomcat**: This is the "Server" that hosts your website. It takes your Java code and makes it accessible through a web browser.
4.  **Maven**: This is the "Manager". It looks at your `pom.xml` file and automatically downloads libraries like the "MySQL Connector" so you don't have to do it manually.

### 2. Project Folders (The Skeleton)
Your project follows a standard "Maven Web App" structure:
-   `src/main/java`: This is where all your **Backend** logic lives. All the `.java` files are here.
-   `src/main/webapp`: This is where your **Frontend** lives. The `.jsp` files (which look like HTML) and any CSS/Images are here.
-   `src/main/webapp/WEB-INF/web.xml`: This is the **Map** of your project. It tells the server which request goes to which Java file.
-   `pom.xml`: This is the list of ingredients Maven needs to build your project.

---

## 🗄️ Phase 2: The Database (`setup.sql`)

The first thing we code is the database. Without a place to store data, the bank doesn't exist. Let's look at `setup.sql` line-by-line.

```sql
CREATE DATABASE IF NOT EXISTS banking_system;
USE banking_system;
```
-   **`CREATE DATABASE`**: This tells MySQL to create a new storage area called `banking_system`.
-   **`IF NOT EXISTS`**: This is a safety check. It prevents an error if the database is already there.
-   **`USE`**: Tells MySQL that "all following commands should happen inside this specific database."

### The `users` Table
```sql
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
-   **`id INT`**: A whole number.
-   **`AUTO_INCREMENT`**: MySQL will automatically give every new user a number (1, 2, 3...). You don't have to type it.
-   **`PRIMARY KEY`**: This makes the `id` the unique fingerprint for that user.
-   **`UNIQUE`**: No two people can sign up with the same email.
-   **`NOT NULL`**: You cannot leave this blank.
-   **`DEFAULT 'user'`**: If you don't specify a role, they are a normal user (not an admin).

---

## 🔌 Phase 3: The Connection (`DatabaseConnection.java`)

Java doesn't naturally know how to talk to MySQL. We need a "bridge". This bridge is found in `com.banking.DB.DatabaseConnection`.

```java
package com.banking.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found!", e);
        }
    }
}
```

### Detailed Breakdown:
1.  **`package com.banking.DB;`**: This tells Java the "address" of this file. It lives in the `com`, `banking`, `DB` folder.
2.  **`import java.sql.*`**: These methods come from the standard Java JDK. They allow Java to handle SQL databases.
3.  **`DB_URL`**: The address of your database. `jdbc:mysql` is the protocol, `localhost` means it's on your computer, and `3306` is the door (port) MySQL is listening on.
4.  **`public static Connection getConnection()`**: 
    -   `public`: Anyone can use this method.
    -   `static`: You don't need to create a `new DatabaseConnection()` object to use it. You just call `DatabaseConnection.getConnection()`.
    -   `throws SQLException`: This warns Java that "this method might fail if the database is offline, so be ready to handle an error."
5.  **`Class.forName("com.mysql.cj.jdbc.Driver")`**: This loads the MySQL "Driver" library. This library comes from the `mysql-connector-java` dependency in your `pom.xml`.
6.  **`DriverManager.getConnection(...)`**: This is the line that actually opens the door to MySQL using your username and password.

---

## 🏁 Phase 4: Opening the Project (The Entry Point)

When you run your project, why does it go straight to the login page? This is controlled by `web.xml`.

### 1. The Welcome File (`web.xml`)
Look at lines 11-13 in your `web.xml`:
```xml
<welcome-file-list>
    <welcome-file>login.jsp</welcome-file>
</welcome-file-list>
```
-   **What this does**: This is a direct instruction to the Tomcat server. It says: "If a user visits the root of my website (like `localhost:8080/demo/`) and doesn't ask for a specific file, automatically show them `login.jsp`."

---

## 🔐 Phase 5: Authentication (Login)

In this project, users do not sign up themselves. **Only the Admin can create new accounts.** This means authentication is only about checking existing "keys" (the Login).

### 1. Understanding `doGet` vs `doPost`

Before we look at the code, you must understand the two ways a browser talks to a Servlet:

1.  **`doGet` (The "View" Request)**: Used when you just want to **see** a page. When you type a URL or click a link, the browser sends a "GET" request.
2.  **`doPost` (The "Action" Request)**: Used when you submit a form (like typing a password and clicking Login). It **sends** data to the server secretly.

---

### 2. Login: Checking the Keys

#### 🎨 The Form (`login.jsp`)
When a user wants to login, they see a form with email and password fields.

#### 🧠 The Logic (`LoginServlet.java`)

Let's look at the "View" part first.

**Line-by-Line: The `doGet` method**
```java
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    request.getRequestDispatcher("/login.jsp").forward(request, response);
}
```
1.  **`HttpServletRequest request`**: This object is like a "Shopping Cart". It holds all the information coming *from* the user (their IP address, what they typed, their session, etc.).
2.  **`HttpServletResponse response`**: This is like a "Blank Delivery Box". We use it to send things *back* to the user (HTML pages, redirects, or errors).
3.  **`throws ServletException, IOException`**: This is Java's way of saying "this code might fail if there's a network error or a server glitch, so handle it carefully."
4.  **`request.getRequestDispatcher("/login.jsp")`**:
    -   Think of this as finding a physical file in your folder.
    -   We are telling Java: "Find the file `/login.jsp` inside my `webapp` folder."
5.  **`.forward(request, response)`**:
    -   This is the "internal handoff". 
    -   It says: "I don't want the Java code to show the page. Give my 'Shopping Cart' (request) and my 'Delivery Box' (response) to the JSP file, and let the JSP draw the HTML for the user."
    -   **Important**: The user's browser URL stays the same. They don't even know a handoff happened!

---

**Line-by-Line: The `doPost` method**
Login is about *selecting* data, not inserting it.

**Line-by-Line Explanation:**
-   **Line 34: `String sql = "SELECT * FROM users WHERE email = ? AND password = ?";`**:
    -   This tells MySQL: "Find a user where the email and password match exactly what was typed."
-   **Line 38: `ResultSet resultSet = statement.executeQuery();`**:
    -   `ResultSet`: This comes from `java.sql`. It's like a table that holds the answer MySQL gave us.
-   **Line 40: `if (resultSet.next()) { ... }`**:
    -   `resultSet.next()`: This method moves the "pointer" to the first row of the result. If it returns `true`, it means we found a matching user!
-   **Line 41: `HttpSession session = request.getSession();`**:
    -   `HttpSession`: This comes from Jakarta EE. It's like a "badge" the user wears while browsing. We use it to remember that they are logged in.
-   **Line 42: `session.setAttribute("userId", resultSet.getInt("id"));`**:
    -   `setAttribute`: This stores the user's ID inside the "badge" so every other page knows who this person is.
-   **Line 45: `if ("admin".equals(resultSet.getString("role"))) { ... }`**:
    -   Here we check if the user is an 'admin' or a 'user' and send them to the correct dashboard.

---

---

## 👔 Phase 6: Admin Features (Managing the Bank)

The Admin is the "Manager". They can add, view, edit, and delete customers. Let's see how this works.

### 1. The Security Pattern (Safety First!)
In most Admin servlets (like `AddCustomerServlet.java`), you will see this `doGet` code:
```java
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    HttpSession session = request.getSession(false);
    if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    request.getRequestDispatcher("/admin/addCustomer.jsp").forward(request, response);
}
```
**Line-by-Line Explanation:**
1.  **`HttpSession session = request.getSession(false);`**: 
    -   We look for the user's "Badge" (Session).
    -   `false` means: "If they don't have a badge, don't give them a new one yet."
2.  **`if (session == null || ...)`**:
    -   We are checking two things: 
        -   Is there no badge at all? (`session == null`)
        -   Is the badge missing the 'admin' rank? (`!"admin".equals(...)`)
3.  **`response.sendRedirect(... + "/login.jsp");`**:
    -   If they aren't a logged-in admin, we kick them out to the login page.
4.  **`return;`**:
    -   Stop everything! Don't let them see the rest of the code.
5.  **`forward(...)`**:
    -   If they **are** a valid admin, we follow the "internal handoff" we learned in Phase 5 to show them the JSP page.

---

### 2. Admin Dashboard (`AdminDashboardServlet.java`)
**Line-by-Line: The `doGet` method**
-   **Line 30-38**: Asks MySQL for two counts: Total Customers and Total Transactions.
-   **Line 40-41**: Attaches these counts (`totalCustomers`, `totalTransactions`) to the request.
-   **Line 43**: Forwards to `/admin/dashboard.jsp` so the admin can see the stats.

### 3. Viewing Customers: The Data Bridge

#### 🧠 The Logic (`ViewCustomersServlet.java`)
This servlet fetches *all* customer data from MySQL and sends it to the JSP.

**Line-by-Line: The `doGet` method**
-   **Line 25: `List<Map<String, Object>> customers = new ArrayList<>();`**:
    -   `List` and `Map`: These come from `java.util`. We use them to create a collection of customers. Each "Map" is one customer's details.
-   **Line 29: `String sql = "SELECT * FROM customers ORDER BY created_at DESC";`**:
    -   Fetches all rows from the `customers` table, newest first.
-   **Line 52: `request.setAttribute("customers", customers);`**:
    -   `setAttribute`: This is very important! It "attaches" our list of customers to the `request` object (the Shopping Cart). This way, the JSP page can see this list.
-   **Line 53: `request.getRequestDispatcher("/admin/viewCustomers.jsp").forward(request, response);`**:
    -   `forward`: The "internal handoff" that sends the Shopping Cart to the JSP.

#### 🎨 The Display (`viewCustomers.jsp`)
In the JSP, we use a loop to show the data:
```jsp
<c:forEach var="customer" items="${customers}">
    <tr>
        <td>${customer.name}</td>
        <td>${customer.balance}</td>
        ...
    </tr>
</c:forEach>
```
-   **`${customers}`**: This looks for the "customers" attribute we attached in the Servlet!

---

### 4. Editing a Customer (`EditCustomerServlet.java`)
**Line-by-Line: The `doGet` method**
-   **Line 34**: Grabs the `accountNumber` from the URL (e.g., `?accountNumber=ACC123`).
-   **Line 39**: Fetches that specific customer's current data from MySQL.
-   **Line 42-47**: Attaches (Set Attribute) their name, email, phone, and address to the request.
-   **Line 55**: Forwards to `/admin/editCustomer.jsp` so the admin can see the data and change it.

### 5. Deleting a Customer: The Post Action

#### 🧠 The Logic (`DeleteCustomerServlet.java`)
**Line-by-Line Explanation:**
-   **Line 21: `String accountNumber = request.getParameter("accountNumber");`**:
    -   Grab the account number of the person we want to delete.
-   **Line 25: `String sql = "DELETE FROM customers WHERE account_number = ?";`**:
    -   The SQL command to remove a row.
-   **Line 31: `response.sendRedirect(... + "/admin/viewCustomers.jsp?msg=deleted");`**:
    -   After deleting, we "refresh" the page by redirecting back to the list.

---

### 6. Adding a Customer (`AddCustomerServlet.java`)
This is very similar to the `RegisterServlet`, but it's only accessible by the Admin.
-   **Line 39: `Double.parseDouble(...)`**:
    -   `parseDouble`: This comes from `java.lang.Double`. It converts the textTyped in the form into a decimal number so we can store it in the database.

---

---

## 💰 Phase 7: User Features (Managing Your Money)

When a customer logs in, they see their dashboard, can send money, and update their profile.

### 1. The User Dashboard (`UserDashboardServlet.java`)
This servlet handles the main home page for a logged-in customer.

**Line-by-Line: The `doGet` method**
-   **Line 25: `HttpSession session = request.getSession(false);`**:
    -   Checks for the user's "Badge" (Session).
-   **Line 26: `Integer userId = (Integer) session.getAttribute("userId");`**:
    -   Reads the ID we saved during login.
-   **Line 34: `SELECT * FROM customers WHERE user_id = ?`**:
    -   Find the customer profile that belongs to this logged-in user.
-   **Line 39-45: `request.setAttribute("customerName", rs.getString("name")); ...`**:
    -   Just like in Phase 6, we "attach" (Set Attribute) all the user's details to the request Shopping Cart.
-   **Line 50: `SELECT * FROM transactions ... LIMIT 5`**:
    -   This fetches only the 5 most recent transactions for this user.
-   **Line 75: `request.getRequestDispatcher("/user/dashboard.jsp").forward(request, response);`**:
    -   The final handoff to the User Dashboard page.

---

### 2. Sending Money (`SendMoneyServlet.java`)
This is the most complex part of the bank logic! It involves three steps: Taking money from one person, giving it to another, and recording the proof.

**Line-by-Line Explanation:**
-   **Line 38: `double amount = Double.parseDouble(request.getParameter("amount"));`**:
    -   Converts the text "100" into a mathematical number `100.0`.
-   **Line 45: `UPDATE customers SET balance = balance - ? WHERE ... balance >= ?`**:
    -   **CRITICAL STEP**: We check if the sender has enough money (`balance >= ?`) before we subtract it. This prevents people from spending money they don't have!
-   **Line 54: `UPDATE customers SET balance = balance + ?`**:
    -   The "Credit" step. We add the money to the receiver's account.
-   **Line 61: `INSERT INTO transactions ... VALUES ('transfer', ?, ?, ?, ?)`**:
    -   We save a permanent record of the transfer in the `transactions` table so it can be seen in the history later.

---

## 🛠️ Phase 8: Admin Transactions (`ManageTransactionServlet.java`)

Admins can also deposit or withdraw money manually for any customer.

**Line-by-Line: The `doGet` method**
-   **Line 22-26**: This is the **Security Check** we saw in Phase 6. It ensures only an Admin can manage transactions.
-   **Line 27**: If safe, it forwards the user to `/admin/manageTransaction.jsp`.

---

## 👤 Phase 9: Profile Updates (`UpdateProfileServlet.java`)

Users can change their name or password. This servlet uses both the `customers` table and the `users` table.

**Line-by-Line: The `doGet` method**
-   **Line 27**: Grabs the user's current session.
-   **Line 32-35**: Asks MySQL for the user's current details so the form isn't empty when they open it.
-   **Line 39-43**: Attaches (Set Attribute) these details to the request.
-   **Line 50**: Forwards to `/user/updateProfile.jsp`.

---

## 📜 Phase 10: Transaction History (`ViewTransactionsServlet.java`)

A bank must keep records! In this project, we have two viewpoints: The Admin (who sees everything) and the User (who sees only their own).

### 1. User View (`ViewTransactionsServlet.java`)
**Line-by-Line: The `doGet` method**
-   **Line 26: `String accountNumber = (String) session.getAttribute("accountNumber");`**:
    -   Finds the logged-in user's account number from their "Badge".
-   **Line 32: `SELECT * FROM transactions WHERE sender_account_number = ? OR receiver_account_number = ?`**:
    -   Fetches both sent and received money.
-   **Line 56**: Attaches the list to the request and forwards to `/user/viewTransactions.jsp`.

### 2. Admin View (`AdminViewTransactionsServlet.java`)
**Line-by-Line: The `doGet` method**
-   **Line 25-29**: The Admin **Security Check**.
-   **Line 39: `SELECT * FROM transactions ORDER BY created_at DESC`**:
    -   Fetches every record in the bank history.
-   **Line 70-71**: Attaches everything and forwards to `/admin/viewTransactions.jsp`.

---

## 🚪 Phase 11: Ending a Session (`LogoutServlet.java`)

When a user clicks "Logout", we need to take away their "Session Badge" so no one else can use their account.

**Line-by-Line Explanation:**
-   **Line 16: `HttpSession session = request.getSession(false);`**:
    -   We find the current active session.
-   **Line 18: `session.invalidate();`**:
    -   `invalidate()`: This is a method from Jakarta EE. It immediately "destroys" the session badge. All saved data like `userId` is wiped out.
-   **Line 21: `response.sendRedirect(... + "/login.jsp");`**:
    -   The user is kicked back to the login page.

---

## 🗺️ Phase 12: The Map (`web.xml`)

How does Tomcat know that typing `/login` in the browser should run `LoginServlet.java`? The map is in `src/main/webapp/WEB-INF/web.xml`.

```xml
<servlet>
    <servlet-name>LoginServlet</servlet-name>
    <servlet-class>com.banking.servlet.LoginServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>LoginServlet</servlet-name>
    <url-pattern>/login</url-pattern>
</servlet-mapping>
```
-   **`<servlet-name>`**: A nickname we give to our Java class.
-   **`<servlet-class>`**: The actual location of your code.
-   **`<url-pattern>`**: What the user types in the browser (e.g., `bank.com/login`).

---

## 🎨 Phase 13: JSP Syntax (The Logic in HTML)

In your `.jsp` files, you'll see special tags like `<% ... %>` and `${...}`.

1.  **Scriptlets `<% ... %>`**: This allows you to write actual Java code inside an HTML page. 
    -   Example: `<% String error = (String) request.getAttribute("error"); %>`
2.  **Expression Language `${...}`**: A shorthand way to show data.
    -   Example: `${customerName}` is the same as saying `request.getAttribute("customerName").toString()`. It makes the code much cleaner!

---

## 🔗 Phase 14: Connecting the Dots (The Request Flow)

To finish, let's trace exactly how a user gets from a click to a page:

1.  **Opening the Browser**: Tomcat looks at `web.xml` -> `<welcome-file-list>` -> Shows `login.jsp`.
2.  **Form Submission**: User types password and clicks "Login".
    -   JSP `<form action="login" method="post">` -> Triggers `LoginServlet.java`'s **`doPost`** method.
3.  **Viewing a Page**: Admin clicks "View Customers".
    -   Link `<a href="viewCustomers">` -> Triggers `ViewCustomersServlet.java`'s **`doGet`** method.
4.  **Internal Handoff**: The Servlet collects data and uses `forward(request, response)` -> The data is handed back to the JSP to be drawn.

---

## 🏁 Final Summary: Where does everything come from?

In this project, you've seen a mix of three worlds:

| Source | Examples | What it provides |
| :--- | :--- | :--- |
| **Standard Java (JDK)** | `double`, `String`, `Map`, `List` | Basic data handling. |
| **Java SQL Library** | `Connection`, `PreparedStatement`, `ResultSet` | Talking to the database. |
| **Jakarta EE / Tomcat** | `HttpServlet`, `request`, `response`, `session` | Handling the Internet (Browsers). |

**Congratulations!** You now know how every line of your Bank Management System works. You understand how the web server starts, how the "Entry Map" (web.xml) directs users, and how Java handles the internal handoffs (Forwarding) between code and pages.
