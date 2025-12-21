# Banking System - Complete Beginner's Guide

## Table of Contents
1. [What is This Project?](#what-is-this-project)
2. [How Does the Project Work?](#how-does-the-project-work)
3. [Understanding JDBC](#understanding-jdbc)
4. [Understanding Servlets](#understanding-servlets)
5. [Understanding Sessions](#understanding-sessions)
6. [Understanding Request Dispatcher](#understanding-request-dispatcher)
7. [Understanding Redirect](#understanding-redirect)
8. [Project Structure](#project-structure)
9. [Code Flow Examples](#code-flow-examples)
10. [Database Tables](#database-tables)
11. [Important Concepts](#important-concepts)

---

## What is This Project?

This is a simple banking system where:
- Users can register and create an account
- Users can login to their account
- Users can send money to other users
- Users can see their transaction history
- Admin can manage all customers and transactions

**Technologies Used:**
- Java (for the backend logic)
- JDBC (to talk to the database)
- Servlets (to handle web requests)
- JSP (to show web pages)
- MySQL (the database)

---

## How Does the Project Work?

### Simple Flow:

1. **User visits website** → Sees login page
2. **User logs in** → System checks email and password in database
3. **If correct** → User goes to dashboard
4. **User can send money** → System updates balances in database
5. **User can see transactions** → System reads from database and shows them

### Step by Step Example (Login):

```
1. User types email and password
2. Clicks "Login" button
3. LoginServlet receives the request
4. LoginServlet connects to database
5. LoginServlet checks if email and password match
6. If match → Save user info in session → Go to dashboard
7. If no match → Show error message
```

---

## Understanding JDBC

### What is JDBC?

**JDBC = Java Database Connectivity**

It's a way for Java programs to talk to databases (like MySQL).

### Think of it like this:
- **Database** = A filing cabinet with folders
- **JDBC** = The way you open the cabinet and read/write files
- **Connection** = Opening the cabinet
- **Statement** = The instruction you give
- **ResultSet** = The papers you get back

### Basic JDBC Steps:

```java
// Step 1: Connect to database
Connection connection = DatabaseConnection.getConnection();

// Step 2: Create a query (like asking a question)
String sql = "SELECT * FROM users WHERE email = ?";
PreparedStatement statement = connection.prepareStatement(sql);
statement.setString(1, "user@example.com");

// Step 3: Run the query and get results
ResultSet resultSet = statement.executeQuery();

// Step 4: Read the results
if (resultSet.next()) {
    String name = resultSet.getString("name");
    int id = resultSet.getInt("id");
}

// Step 5: Close everything (IMPORTANT!)
resultSet.close();
statement.close();
connection.close();
```

### Why Use PreparedStatement?

**PreparedStatement** is safer than regular Statement because:
- It prevents SQL injection attacks
- It's faster for repeated queries
- It handles special characters automatically

**Example:**
```java
// BAD (unsafe):
String sql = "SELECT * FROM users WHERE email = '" + email + "'";

// GOOD (safe):
String sql = "SELECT * FROM users WHERE email = ?";
PreparedStatement stmt = connection.prepareStatement(sql);
stmt.setString(1, email);
```

### Common JDBC Operations:

#### 1. SELECT (Read data)
```java
String sql = "SELECT * FROM customers WHERE id = ?";
PreparedStatement stmt = connection.prepareStatement(sql);
stmt.setInt(1, customerId);
ResultSet rs = stmt.executeQuery();

if (rs.next()) {
    String name = rs.getString("name");
    double balance = rs.getDouble("balance");
}
```

#### 2. INSERT (Add new data)
```java
String sql = "INSERT INTO users (email, password, role) VALUES (?, ?, ?)";
PreparedStatement stmt = connection.prepareStatement(sql);
stmt.setString(1, email);
stmt.setString(2, password);
stmt.setString(3, "user");
int rowsInserted = stmt.executeUpdate(); // Returns number of rows added
```

#### 3. UPDATE (Change existing data)
```java
String sql = "UPDATE customers SET balance = balance + ? WHERE account_number = ?";
PreparedStatement stmt = connection.prepareStatement(sql);
stmt.setDouble(1, 100.0); // Add 100
stmt.setString(2, "ACC123");
int rowsUpdated = stmt.executeUpdate(); // Returns number of rows changed
```

#### 4. DELETE (Remove data)
```java
String sql = "DELETE FROM customers WHERE account_number = ?";
PreparedStatement stmt = connection.prepareStatement(sql);
stmt.setString(1, "ACC123");
int rowsDeleted = stmt.executeUpdate(); // Returns number of rows deleted
```

---

## Understanding Servlets

### What is a Servlet?

A **Servlet** is a Java class that handles web requests.

**Think of it like this:**
- **Web Browser** = Customer
- **Servlet** = Waiter in a restaurant
- **Request** = Customer's order
- **Response** = Food served to customer

### How Servlets Work:

```
1. User clicks a button or visits a page
2. Browser sends a request to the server
3. Server finds the right Servlet
4. Servlet processes the request (maybe talks to database)
5. Servlet sends a response back
6. Browser shows the result to user
```

### Two Main Methods:

#### 1. doGet() - For viewing pages
```java
protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    // This runs when user visits the page (like clicking a link)
    // Usually just shows a page
    request.getRequestDispatcher("/login.jsp").forward(request, response);
}
```

#### 2. doPost() - For submitting forms
```java
protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    // This runs when user submits a form (like clicking "Login" button)
    // Usually processes data and saves to database
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    // ... process login ...
}
```

### Getting Data from Forms:

```java
// If form has: <input name="email" value="user@example.com">
String email = request.getParameter("email"); // Gets "user@example.com"

// If form has: <input name="amount" value="100">
String amountStr = request.getParameter("amount"); // Gets "100" (as String)
double amount = Double.parseDouble(amountStr); // Convert to number
```

### Sending Data to Pages:

```java
// Put data in request so JSP page can use it
request.setAttribute("error", "Invalid password");
request.setAttribute("balance", 1000.50);

// Then forward to page
request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
```

---

## Understanding Sessions

### What is a Session?

A **Session** is like a memory that remembers who is logged in.

**Think of it like this:**
- **Session** = A name tag at a party
- When you arrive, you get a name tag
- Everyone can see your name tag
- When you leave, you give back the name tag

### Why Do We Need Sessions?

**Problem:** Web pages don't remember who you are between requests.

**Solution:** Session stores your information on the server.

### How Sessions Work:

```
1. User logs in
2. Server creates a session
3. Server stores user info in session (like userId, email, role)
4. Server gives browser a session ID (like a ticket)
5. Browser sends session ID with every request
6. Server uses session ID to remember who you are
7. When user logs out, session is deleted
```

### Session Code Examples:

#### Creating and Using Session:
```java
// Get or create session
HttpSession session = request.getSession();

// Store data in session
session.setAttribute("userId", 123);
session.setAttribute("userEmail", "user@example.com");
session.setAttribute("userRole", "user");
session.setAttribute("balance", 1000.50);

// Get data from session (in another servlet)
HttpSession session = request.getSession(false); // false = don't create if doesn't exist
Integer userId = (Integer) session.getAttribute("userId");
String email = (String) session.getAttribute("userEmail");
String role = (String) session.getAttribute("userRole");

// Check if user is logged in
if (session == null || !"user".equals(session.getAttribute("userRole"))) {
    response.sendRedirect("/login.jsp");
    return;
}

// Delete session (logout)
session.invalidate();
```

### Session vs Request:

| Request | Session |
|---------|---------|
| Data lasts for ONE request | Data lasts for MANY requests |
| Lost when page changes | Saved until logout |
| Used for temporary data | Used for login info |
| Example: Error message | Example: User ID |

---

## Understanding Request Dispatcher

### What is Request Dispatcher?

**Request Dispatcher** is used to show a JSP page to the user.

**Think of it like this:**
- **Request Dispatcher** = A waiter bringing food to your table
- You tell the waiter which page to show
- The waiter brings that page to the user
- The URL in browser stays the same

### How to Use It:

```java
// Show a page to the user
request.getRequestDispatcher("/login.jsp").forward(request, response);

// You can also send data with it
request.setAttribute("error", "Wrong password");
request.getRequestDispatcher("/login.jsp").forward(request, response);
```

### When to Use Request Dispatcher:

- When you want to show a page
- When you want to keep the same URL
- When you want to send data to the page

**Example:**
```java
// User submits login form
// Check if login is correct
if (loginCorrect) {
    // Show dashboard page
    request.getRequestDispatcher("/user/dashboard.jsp").forward(request, response);
} else {
    // Show error and stay on login page
    request.setAttribute("error", "Wrong password");
    request.getRequestDispatcher("/login.jsp").forward(request, response);
}
```

---

## Understanding Redirect

### What is Redirect?

**Redirect** tells the browser to go to a different page.

**Think of it like this:**
- **Redirect** = A sign that says "Go to Room 5"
- You follow the sign
- You end up at a different place
- The URL in browser changes

### How to Use It:

```java
// Send user to a different page
response.sendRedirect(request.getContextPath() + "/login.jsp");

// Or
response.sendRedirect("/admin/dashboard.jsp");
```

### When to Use Redirect:

- After successfully saving data
- After login/logout
- When you want the URL to change
- To prevent form resubmission

**Example:**
```java
// User logs in successfully
if (loginCorrect) {
    // Save user info in session
    session.setAttribute("userId", userId);
    
    // Send user to dashboard (URL changes to /dashboard.jsp)
    response.sendRedirect(request.getContextPath() + "/user/dashboard.jsp");
}
```

### Redirect vs Request Dispatcher:

| Request Dispatcher | Redirect |
|-------------------|----------|
| URL stays the same | URL changes |
| Server shows page | Browser requests new page |
| Can send data | Cannot send data easily |
| Use for: Showing pages | Use for: After actions |

**Example:**
```java
// After deleting a customer
// Use redirect to go back to customer list
// This prevents accidentally deleting again if user refreshes
response.sendRedirect("/admin/viewCustomers.jsp");
```

---

## Project Structure

```
demo/
├── src/main/java/com/banking/
│   ├── servlet/          (All servlets that handle requests)
│   │   ├── LoginServlet.java
│   │   ├── RegisterServlet.java
│   │   ├── SendMoneyServlet.java
│   │   └── ... (other servlets)
│   └── util/             (Helper classes)
│       ├── DatabaseConnection.java
│       └── PasswordUtil.java
├── src/main/webapp/      (Web pages)
│   ├── login.jsp
│   ├── register.jsp
│   ├── user/
│   │   ├── dashboard.jsp
│   │   └── sendMoney.jsp
│   └── admin/
│       ├── dashboard.jsp
│       └── viewCustomers.jsp
└── database/
    └── setup.sql         (Database structure)
```

---

## Code Flow Examples

### Example 1: User Login Flow

```
1. User visits: http://localhost:8080/login
   → LoginServlet.doGet() runs
   → Shows login.jsp page

2. User types email and password, clicks "Login"
   → LoginServlet.doPost() runs
   
3. LoginServlet gets email and password from form:
   String email = request.getParameter("email");
   String password = request.getParameter("password");
   
4. LoginServlet connects to database:
   Connection conn = DatabaseConnection.getConnection();
   
5. LoginServlet checks if user exists:
   String sql = "SELECT * FROM users WHERE email = ?";
   PreparedStatement stmt = conn.prepareStatement(sql);
   stmt.setString(1, email);
   ResultSet rs = stmt.executeQuery();
   
6. If user found, check password:
   if (rs.next()) {
       String storedPassword = rs.getString("password");
       if (password.equals(storedPassword)) {
           // Password correct!
       }
   }
   
7. If correct, save in session:
   HttpSession session = request.getSession();
   session.setAttribute("userId", userId);
   session.setAttribute("userRole", "user");
   
8. Redirect to dashboard:
   response.sendRedirect("/user/dashboard.jsp");
```

### Example 2: Sending Money Flow

```
1. User visits send money page
   → SendMoneyServlet.doGet() runs
   → Shows sendMoney.jsp

2. User fills form: receiver account, amount, note
   → Clicks "Send Money"
   → SendMoneyServlet.doPost() runs

3. Get form data:
   String receiverAccount = request.getParameter("receiverAccountNumber");
   String amountStr = request.getParameter("amount");
   double amount = Double.parseDouble(amountStr);

4. Get sender account from session:
   String senderAccount = (String) session.getAttribute("accountNumber");

5. Check if receiver exists:
   String sql = "SELECT COUNT(*) FROM customers WHERE account_number = ?";
   // If count > 0, receiver exists

6. Check if sender has enough money:
   String sql = "SELECT balance FROM customers WHERE account_number = ?";
   // If balance >= amount, can send

7. Add transaction to database:
   String sql = "INSERT INTO transactions ...";
   stmt.executeUpdate();

8. Update sender balance (subtract):
   String sql = "UPDATE customers SET balance = balance - ? WHERE account_number = ?";

9. Update receiver balance (add):
   String sql = "UPDATE customers SET balance = balance + ? WHERE account_number = ?";

10. Show success message:
    request.setAttribute("success", "Money sent!");
    request.getRequestDispatcher("/user/sendMoney.jsp").forward(request, response);
```

---

## Database Tables

### 1. users Table
Stores login information.

| Column | Type | Description |
|--------|------|-------------|
| id | INT | Unique number for each user |
| email | VARCHAR | User's email (used for login) |
| password | VARCHAR | User's password (plain text) |
| role | VARCHAR | Either "user" or "admin" |

### 2. customers Table
Stores customer account information.

| Column | Type | Description |
|--------|------|-------------|
| id | INT | Unique number |
| account_number | VARCHAR | Bank account number (like "ACC123") |
| user_id | INT | Links to users table |
| name | VARCHAR | Customer's name |
| email | VARCHAR | Customer's email |
| phone | VARCHAR | Phone number |
| address | TEXT | Address |
| balance | DECIMAL | How much money they have |

### 3. transactions Table
Stores all money transactions.

| Column | Type | Description |
|--------|------|-------------|
| id | INT | Unique number |
| transaction_type | VARCHAR | "deposit", "withdrawal", or "transfer" |
| sender_account_number | VARCHAR | Who sent the money |
| receiver_account_number | VARCHAR | Who received the money |
| amount | DECIMAL | How much money |
| note | TEXT | Optional message |
| created_at | TIMESTAMP | When it happened |

---

## Important Concepts

### 1. Always Close Database Connections

**Why?** If you don't close connections, your database will run out of connections and crash.

**How?** Use try-finally:
```java
Connection conn = null;
PreparedStatement stmt = null;
ResultSet rs = null;

try {
    conn = DatabaseConnection.getConnection();
    // ... do database work ...
} catch (Exception e) {
    e.printStackTrace();
} finally {
    // Always close, even if error happens
    try {
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (conn != null) conn.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

### 2. Check if User is Logged In

**Why?** To prevent unauthorized access.

**How?** Check session before doing anything:
```java
HttpSession session = request.getSession(false);
if (session == null || !"user".equals(session.getAttribute("userRole"))) {
    response.sendRedirect("/login.jsp");
    return; // Stop here, don't continue
}
```

### 3. Validate Input Data

**Why?** To prevent errors and security issues.

**How?** Always check if data exists and is valid:
```java
String email = request.getParameter("email");
if (email == null || email.trim().isEmpty()) {
    request.setAttribute("error", "Please enter email");
    request.getRequestDispatcher("/register.jsp").forward(request, response);
    return;
}
```

### 4. Use PreparedStatement for Safety

**Why?** Prevents SQL injection attacks.

**BAD:**
```java
String sql = "SELECT * FROM users WHERE email = '" + email + "'";
```

**GOOD:**
```java
String sql = "SELECT * FROM users WHERE email = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, email);
```

### 5. Handle Errors Gracefully

**Why?** Users should see friendly error messages, not technical errors.

**How?** Catch exceptions and show user-friendly messages:
```java
try {
    // ... database work ...
} catch (Exception e) {
    e.printStackTrace(); // Log error for developer
    request.setAttribute("error", "Something went wrong. Please try again.");
    request.getRequestDispatcher("/page.jsp").forward(request, response);
}
```

---

## Common Patterns in This Project

### Pattern 1: Login Check
```java
HttpSession session = request.getSession(false);
if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
    response.sendRedirect("/login.jsp");
    return;
}
```

### Pattern 2: Get Data from Database
```java
Connection conn = DatabaseConnection.getConnection();
String sql = "SELECT * FROM table WHERE id = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setInt(1, id);
ResultSet rs = stmt.executeQuery();

if (rs.next()) {
    String name = rs.getString("name");
    // ... use the data ...
}
```

### Pattern 3: Save Data to Database
```java
Connection conn = DatabaseConnection.getConnection();
String sql = "INSERT INTO table (column1, column2) VALUES (?, ?)";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, value1);
stmt.setString(2, value2);
int rows = stmt.executeUpdate();

if (rows > 0) {
    // Success!
}
```

### Pattern 4: Update Data in Database
```java
Connection conn = DatabaseConnection.getConnection();
String sql = "UPDATE table SET column1 = ? WHERE id = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, newValue);
stmt.setInt(2, id);
stmt.executeUpdate();
```

---

## Summary

### Key Takeaways:

1. **JDBC** = How Java talks to database
   - Connection → Statement → ResultSet → Close

2. **Servlets** = Handle web requests
   - doGet() for viewing pages
   - doPost() for submitting forms

3. **Sessions** = Remember who is logged in
   - Store user info
   - Check before allowing access

4. **Request Dispatcher** = Show a page
   - URL stays same
   - Can send data

5. **Redirect** = Go to different page
   - URL changes
   - Use after actions

6. **Always**:
   - Close database connections
   - Check if user is logged in
   - Validate input data
   - Handle errors gracefully

---

## Practice Exercises

1. **Add a new feature**: Add a "View Balance" button that shows only the balance
2. **Modify existing feature**: Change password minimum from 8 to 6 characters
3. **Add validation**: Check if email format is valid before registration
4. **Add error handling**: Show specific error messages for different problems

---

## Need Help?

If you're stuck:
1. Check the error message in console
2. Make sure database is running
3. Check if all connections are closed
4. Verify session attributes are set correctly
5. Check if form field names match parameter names

---


