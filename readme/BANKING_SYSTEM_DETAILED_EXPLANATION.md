# Banking System 

## 1. Authentication Servlets

### LoginServlet.java
**Goal**: Check if user exists and log them in.
**Code Flow:**
1.  `String email = request.getParameter("email");`
    *   **What it does**: Grabs the email typed in the login box.
2.  `Connection connection = ...`
    *   **What it does**: Opens a line to the MySQL database.
3.  `String sql = "SELECT * FROM users WHERE email = ? AND password = ?";`
    *   **What it does**: Prepares a question: "Is there a user with this exact email AND password?"
4.  `statement.setString(1, email); ...`
    *   **What it does**: Fills in the `?` blanks safely.
5.  `ResultSet resultSet = statement.executeQuery();`
    *   **What it does**: Sends the question. `resultSet` will contain the user row if found.
6.  `if (resultSet.next()) { ... }`
    *   **What it does**: Checks "Did we find a match?".
7.  `HttpSession session = request.getSession();`
    *   **What it does**: Asks the server to start a "Session" memory for this user.
8.  `session.setAttribute("userId", ...);`
    *   **What it does**: Saves the User ID in that memory so we don't forget who they are.
9.  `if ("admin".equals(role)) { response.sendRedirect(...); }`
    *   **What it does**: If they are an admin, sends them to Admin Dashboard. Otherwise, User Dashboard.

### RegisterServlet.java
**Goal**: Create a new User and a new Customer profile.
**Code Flow:**
1.  `String name = request.getParameter("name"); ...`
    *   **What it does**: Reads all the form fields.
2.  `String sqlUser = "INSERT INTO users ...";`
    *   **What it does**: Prepares to add the Login Credentials first.
3.  `stmt.executeUpdate();`
    *   **What it does**: Saves the email/password to the `users` table.
4.  `ResultSet rs = stmt.getGeneratedKeys();`
    *   **What it does**: Asks the DB "What ID did you just give this new user?".
5.  `String sqlCust = "INSERT INTO customers ...";`
    *   **What it does**: Prepares to add the Profile Details (Name, Phone, etc.).
6.  `stmt.setInt(2, userId);`
    *   **What it does**: Links this Profile to the User ID we just got.
7.  `stmt.executeUpdate();`
    *   **What it does**: Saves the profile.
8.  `response.sendRedirect(...);`
    *   **What it does**: Sends specific success message to the dashboard.

### LogoutServlet.java
**Goal**: Sign out.
**Code Flow:**
1.  `HttpSession session = request.getSession(false);`
    *   **What it does**: Checks if a session currently exists.
2.  `if (session != null) { session.invalidate(); }`
    *   **What it does**: If yes, destroy it. Wipes all saved memory.
3.  `response.sendRedirect("login.jsp");`
    *   **What it does**: Sends user back to login screen.

---

## 2. Admin Features

### AdminDashboardServlet.java
**Goal**: Show total counts.
**Code Flow:**
1.  `rs = stmt.executeQuery("SELECT COUNT(*) FROM customers");`
    *   **What it does**: Asks DB "How many customers exist?".
2.  `if (rs.next()) totalCustomers = rs.getInt(1);`
    *   **What it does**: Reads the number (e.g., "10").
3.  `request.setAttribute("totalCustomers", totalCustomers);`
    *   **What it does**: Passes this number "10" to the HTML page.
4.  `request.getRequestDispatcher(...).forward(...);`
    *   **What it does**: Loads the dashboard JSP page with the data we just passed.

### ManageTransactionServlet.java
**Goal**: Deposit, Withdraw, or Transfer money.
**Code Flow (Deposit Example):**
1.  `String type = request.getParameter("transactionType");`
    *   **What it does**: Checks if admin clicked "Deposit", "Withdraw", etc.
2.  `if ("deposit".equals(type))`
    *   **What it does**: If it is a Deposit...
3.  `String sql = "UPDATE customers SET balance = balance + ? ...";`
    *   **What it does**: Adds the amount to the current balance directly in the DB.
4.  `stmt.executeUpdate();`
    *   **What it does**: Saves the new balance.
5.  `String txnSql = "INSERT INTO transactions ...";`
    *   **What it does**: Creates a record in history saying "Admin deposited $X".

### ViewCustomersServlet.java
**Goal**: Show a list of everyone.
**Code Flow:**
1.  `String sql = "SELECT * FROM customers ...";`
    *   **What it does**: Asks for every single customer row.
2.  `while (resultSet.next()) { ... }`
    *   **What it does**: Loops through every row found.
3.  `customers.add(customer);`
    *   **What it does**: Adds that customer to a big List.
4.  `request.setAttribute("customers", customers);`
    *   **What it does**: Sends the big List to the JSP to draw the table.

### EditCustomerServlet.java
**Goal**: Change a customer's details.
**Code Flow (DoPost - Saving):**
1.  `String name = request.getParameter("name");`
    *   **What it does**: Gets the new name/email typed by Admin.
2.  `String sql = "UPDATE customers SET name = ?, ... WHERE account_number = ?";`
    *   **What it does**: Overwrites the old data with new data in the DB.
3.  `response.sendRedirect(...);`
    *   **What it does**: Goes back to the edit page with a "Success" message.

### DeleteCustomerServlet.java
**Goal**: Delete a customer.
**Code Flow:**
1.  `String sql = "DELETE FROM customers WHERE account_number = ?";`
    *   **What it does**: Completely removes the row for that account number.
2.  `response.sendRedirect(...);`
    *   **What it does**: Refreshes the list.

---

## 3. User Features

### UserDashboardServlet.java
**Goal**: Show my info and my recent history.
**Code Flow:**
1.  `Integer userId = (Integer) session.getAttribute("userId");`
    *   **What it does**: "Who is logged in right now?"
2.  `SELECT * FROM customers WHERE user_id = ?`
    *   **What it does**: Finds the profile linked to this User ID.
3.  `request.setAttribute("balance", ...);`
    *   **What it does**: Sends my balance to the screen.
4.  `SELECT * FROM transactions ... LIMIT 5`
    *   **What it does**: Grabs my last 5 transactions.
5.  `request.setAttribute("recentTransactions", ...);`
    *   **What it does**: Sends those 5 items to the screen.

### SendMoneyServlet.java
**Goal**: Move money from Me -> Them.
**Code Flow:**
1.  `String receiverAccount = request.getParameter("receiverAccount");`
    *   **What it does**: Who are we sending to?
2.  `UPDATE customers SET balance = balance - ? WHERE account_number = ME`
    *   **What it does**: Takes money OUT of my account.
3.  `UPDATE customers SET balance = balance + ? WHERE account_number = THEM`
    *   **What it does**: Puts money INTO their account.
4.  `INSERT INTO transactions ...`
    *   **What it does**: Log it.
5.  `response.sendRedirect(...);`
    *   **What it does**: Done.

### UpdateProfileServlet.java
**Goal**: Change my own info.
**Code Flow:**
1.  `UPDATE customers SET phone = ?, address = ? ...`
    *   **What it does**: Updates my contact info.
2.  `if (password != null) UPDATE users SET password = ? ...`
    *   **What it does**: If I typed a new password, update my login credentials too.

---

## 4. Key Java Concepts Explained Simply

Here is the "Dictionary" for the special Java commands we use everywhere.

### 1. `response.sendRedirect("page.jsp")`
*   **Simple Logic**: "Stop what you are doing, reset everything, and go to this new URL."
*   **When we use it**: After we finish *changing* data (like Login, Register, Deposit).
*   **Why**: If the user hits "Refresh" after a redirect, it won't accidentally charge them twice. It starts a completely fresh request.

### 2. `request.getRequestDispatcher("page.jsp").forward(request, response)`
*   **Simple Logic**: "Keep the current data alive and just show this HTML page to the user."
*   **When we use it**: When we want to *show* data (like "View Customers" or "Dashboard").
*   **Why**: We need to pass data (like the list of customers) from the Java code to the JSP page. A Redirect would lose that data; a Forward keeps it safe.

### 3. `HttpSession`
*   **Simple Logic**: A digital "backpack" that stays with the user as they browse.
*   **When we use it**: To remember who is logged in.
*   **How it works**:
    *   **Login**: We ask the server for the backpack (`getSession`) and put the User ID inside (`setAttribute`).
    *   **Dashboard**: We open the backpack (`getAttribute`) to see who fits this ID.
    *   **Logout**: We throw the backpack away (`invalidate`).

### 4. `web.xml`
*   **Simple Logic**: The "Map" or "Phonebook" for the server.
*   **What it does**: It tells the server which Java class handles which URL.
*   **Example**:
    ```xml
    <servlet-mapping>
        <servlet-name>LoginServlet</servlet-name>
        <url-pattern>/login</url-pattern>
    </servlet-mapping>
    ```
    *   **Translation**: "Hey Server, when a user visits `/login`, please wake up `LoginServlet.java` to handle it."
